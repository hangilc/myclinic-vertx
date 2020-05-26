function ajaxGet(url, data) {
    return new Promise((resolve, fail) => {
        $.ajax({
            type: "GET",
            url: url,
            data: data,
            cache: false,
            dataType: "json",
            success: resolve,
            error: (xhr, status, err) => fail(err)
        });
    });
}

function ajaxPost(url, data, encodeJson=true) {
    return new Promise((resolve, fail) => {
        let dataValue = encodeJson ? JSON.stringify(data) : data;
        $.ajax({
            type: "POST",
            url: url,
            data: dataValue,
            cache: false,
            dataType: "json",
            success: resolve,
            error: (xhr, status, err) => fail(err)
        });
    });
}

class Rest {
    constructor(baseUrl){
        this.baseUrl = baseUrl;
    }

    url(path){
        return this.baseUrl + path;
    }

    async listWqueueFull(){
        return await ajaxGet(this.url("/list-wqueue-full"), {});
    }

    async getMeisai(visitId){
        return await ajaxGet(this.url("/get-visit-meisai"), {"visit-id": visitId});
    }

    async finishCharge(visitId, amount, payTime){
        if( moment.isMoment(payTime) ){
            payTime = payTime.format("YYYY-MM-DD HH:mm:ss");
        }
        if( typeof payTime !== "string" ){
            throw `Invalid paytime: ${payTime}`;
        }
        let dto = {
            visitId: visitId,
            amount: amount,
            paytime: payTime
        }
        return await ajaxPost(this.url("/finish-cashier"), dto);
    }

    async searchPatient(text){
        return await ajaxGet(this.url("/search-patient"), {text: text});
    }

}

const WqueueStateWaitExam = 0;
const WqueueStateInExam = 1;
const WqueueStateWaitCashier = 2;
const WqueueStateWaitDrug = 3;
const WqueueStateWaitReExam = 4;
const WqueueStateWaitAppoint = 5;

const WqueueStateRep = {
    [WqueueStateWaitExam]: "診待",
    [WqueueStateInExam]: "診中",
    [WqueueStateWaitCashier]: "会待",
    [WqueueStateWaitDrug]: "薬待",
    [WqueueStateWaitReExam]: "再待"
};

function wqueueStateCodeToRep(code){
    return WqueueStateRep[code];
}

function sexToRep(sex){
    switch(sex){
        case "M": return "男";
        case "F": return "女";
        default: return sex;
    }
}


function replaceElement(prevElement, newElement){
    prevElement.after(newElement);
    prevElement.detach();
}

