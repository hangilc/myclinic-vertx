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
            error: (xhr, status, err) => {
                alert(err + "\n" + xhr.responseText);
                fail(err);
            }
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

    async endExam(visitId, charge){
        return await ajaxGet(this.url("/end-exam"), {"visit-id": visitId, "charge": charge});
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

    async startVisit(patientId) {
        return await ajaxGet(this.url("/start-visit"), {"patient-id": patientId});
    }

    async listVisit(patientId, page) {
        return await ajaxGet(this.url("/list-visit-full2"), {"patient-id": patientId, page: page});
    }

    async enterText(text) {
        return await ajaxPost(this.url("/enter-text"), text);
    }

    async updateText(text) {
        return await ajaxPost(this.url("/update-text"), text);
    }

    async getText(textId) {
        return await ajaxGet(this.url("/get-text"), {"text-id": textId});
    }

    async listText(visitId) {
        return await this.get("/list-text", { "visit-id": visitId });
    }

    async deleteText(textId) {
        return await this.post("/delete-text", {}, {
            params: {
                "text-id": textId
            }
        });
    }

    async getHoken(visitId){
        return await this.get("/get-hoken", {"visit-id": visitId});
    }

    async hokenRep(hoken) {
        return await ajaxPost(this.url("/hoken-rep"), hoken);
    }

    async getVisit(visitId){
        return await this.get("/get-visit", {"visit-id": visitId});
    }

    async deleteVisit(visitId){
        return await this.post("/delete-visit", {}, {
            params: {"visit-id": visitId}
        });
    }

    async getPatient(patientId){
        return await this.get("/get-patient", {"patient-id": patientId});
    }

    async calcRcptAge(birthday, at){
        return await this.get("/calc-rcpt-age", {"birthday": birthday, "at": at});
    }

    async calcFutanWari(hoken, rcptAge){
        let req = {
            hoken: hoken,
            rcptAge: rcptAge
        };
        return await this.post("/calc-futan-wari", req);
    }

    async printDrawer(pages){
        return await this.post("/print-drawer", pages);
    }

    async saveDrawerAsPdf(pages, paperSize, savePath, ops){
        let req = {
            pages,
            paperSize,
            savePath
        }
        Object.assign(req, ops);
        return await this.post("/save-drawer-as-pdf", req);
    }

    async shohousenDrawer(shohousenRequest){
        return await this.post("/shohousen-drawer", shohousenRequest);
    }

    async shohousenGrayStampInfo(){
        return await this.get("/shohousen-gray-stamp-info");
    }

    async convertToRomaji(text){
        let result = await this.get("/convert-to-romaji", {text: text});
        return result.value;
    }

    async getShohousenSavePdfPath(name, textId, patientId, date){
        let result = await this.get("/get-shohousen-save-pdf-path", {
            "name": name,
            "text-id": textId,
            "patient-id": patientId,
            "date": date,
            "mkdir": true
        });
        return result.value;
    }

    async suspendExam(visitId){
        return await this.get("/suspend-exam", { "visit-id": visitId });
    }

    async startExam(visitId){
        return await this.get("/start-exam", { "visit-id": visitId });
    }

    async listRecentVisitWithPatient(page){
        if( page == null ){
            page = 0;
        }
        return await this.get("/list-recent-visit-with-patient", {page: page});
    }

    async listTodaysVisits(){
        return await this.get("/list-todays-visits", {});
    }

    async listVisitPatientAt(at){
        return await this.get("/list-visit-patient-at", {at: at});
    }

    async batchEnterShinryouByNames(names, visitId){
        return await this.get("/batch-enter-shinryou-by-name", {
            name: names,
            "visit-id": visitId
        });
    }

    async listShinryouFullByIds(shinryouIds){
        return await this.get("/list-shinryou-full-by-ids", {"shinryou-id": shinryouIds});
    }

    async listDrugFullByIds(drugIds){
        return await this.get("/list-drug-full-by-drug-ids", {"drug-id": drugIds});
    }

    async listConductFullByIds(conductIds){
        return await this.get("/list-conduct-full-by-ids", {"conduct-id": conductIds});
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


