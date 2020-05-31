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

function ajaxPost(url, data, encodeJson = true) {
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

class Client {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }

    url(path) {
        return this.baseUrl + path;
    }

    async get(path, data) {
        return await ajaxGet(this.url(path), data);
    }

    async post(path, data, opts = null) {
        let encodeData = true;
        if (opts == null) {
            opts = {};
        }
        let url = this.url(path);
        if (opts.preventDataEncoding) {
            encodeData = false;
        }
        if (opts.params) {
            let parts = [];
            for (let key of Object.keys(opts.params)) {
                key = encodeURIComponent(key);
                let val = encodeURIComponent(opts.params[key]);
                parts.push(`${key}=${val}`);
            }
            url += "?" + parts.join("&");
        }
        return await ajaxPost(url, data, encodeData);
    }

}

class Rest extends Client {
    constructor(baseUrl) {
        super(baseUrl);
    }

    async listWqueueFull() {
        return await ajaxGet(this.url("/list-wqueue-full"), {});
    }

    async getMeisai(visitId) {
        return await ajaxGet(this.url("/get-visit-meisai"), {"visit-id": visitId});
    }

    async finishCharge(visitId, amount, payTime) {
        if (moment.isMoment(payTime)) {
            payTime = payTime.format("YYYY-MM-DD HH:mm:ss");
        }
        if (typeof payTime !== "string") {
            throw `Invalid paytime: ${payTime}`;
        }
        let dto = {
            visitId: visitId,
            amount: amount,
            paytime: payTime
        }
        return await ajaxPost(this.url("/finish-cashier"), dto);
    }

    async getClinicInfo() {
        return await this.get("/get-clinic-info", {});
    }

    async searchPatient(text) {
        return await ajaxGet(this.url("/search-patient"), {text: text});
    }

    async startVisit(patientId){
        return await ajaxGet(this.url("/start-visit"), {"patient-id": patientId});
    }

    async listVisit(patientId, page){
        return await ajaxGet(this.url("/list-visit-full2"), {"patient-id": patientId, page: page});
    }

}

class Integration extends Client {
    constructor(baseUrl) {
        super(baseUrl);
    }

    async getHoumonKangoClinicParam() {
        return await this.get("/houmon-kango/get-clinic-param");
    }

    async getHoumonKangoRecord(patientId) {
        return await this.get("/houmon-kango/get-record", {"patient-id": patientId});
    }

    async saveHoumonKangoRecord(record) {
        let patientId = record.patientId;
        if (!patientId) {
            throw new Error(`"Cannot find patientId: ${record}`);
        }
        let data = JSON.stringify(record, null, 2);
        return await this.post("/houmon-kango/save-record", data,
            {
                params: {"patient-id": patientId},
                preventDataEncoding: true
            });
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

function wqueueStateCodeToRep(code) {
    return WqueueStateRep[code];
}

function sexToRep(sex) {
    switch (sex) {
        case "M":
            return "男";
        case "F":
            return "女";
        default:
            return sex;
    }
}


function replaceElement(prevElement, newElement) {
    prevElement.after(newElement);
    prevElement.detach();
}

