function ajaxGet(url, data) {
    return new Promise((resolve, fail) => {
        $.ajax({
            type: "GET",
            url: url,
            data: data,
            cache: false,
            dataType: "json",
            success: resolve,
            error: (xhr, status, err) => {
                let msg = xhr.responseText || err.toString() || status;
                alert(msg);
                fail(msg);
            }
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
                let msg = xhr.responseText + " : " + err.toString() + " : " + status;
                alert(msg);
                fail(msg);
            }
        });
    });
}

class Client {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }

    url(path, params) {
        let url = this.baseUrl + path;
        if (params) {
            let parts = [];
            for (let key of Object.keys(params)) {
                key = encodeURIComponent(key);
                let val = encodeURIComponent(params[key]);
                parts.push(`${key}=${val}`);
            }
            url += "?" + parts.join("&");
        }
        return url;
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

    async updatePatient(patient){
        return await this.post("/update-patient", patient);
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

    async shahokokuhoRep(shahokokuho) {
        return await ajaxPost(this.url("/shahokokuho-rep"), shahokokuho);
    }

    async koukikoureiRep(koukikourei) {
        return await ajaxPost(this.url("/koukikourei-rep"), koukikourei);
    }

    async roujinRep(roujin) {
        return await ajaxPost(this.url("/roujin-rep"), roujin);
    }

    async kouhiRep(kouhi) {
        return await ajaxPost(this.url("/kouhi-rep"), kouhi);
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
        return await this.get("/get-shohousen-save-pdf-path", {
            "name": name,
            "text-id": textId,
            "patient-id": patientId,
            "date": date,
            "mkdir": true
        });
    }

    async saveShohousenPdf(shohousenRequest, textId){
        return await this.post("/save-shohousen-pdf", shohousenRequest,
            { params: {"text-id": textId} });
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

    async deleteShinryou(shinryouId){
        return await this.get("/delete-shinryou", {"shinryou-id": shinryouId});
    }

    async listDrugFullByIds(drugIds){
        return await this.get("/list-drug-full-by-drug-ids", {"drug-id": drugIds});
    }

    async listConductFullByIds(conductIds){
        return await this.get("/list-conduct-full-by-ids", {"conduct-id": conductIds});
    }

    async probeShohousenFaxImage(textId, date){
        return await this.get("/probe-shohousen-fax-image", {"text-id": textId, "date": date});
    }

    async sendFax(faxNumber, pdfFile){
        return await this.get("/send-fax", {"fax-number": faxNumber, "pdf-file": pdfFile});
    }

    async pollFax(faxSid){
        return await this.get("/poll-fax", {"fax-sid": faxSid});
    }

    async listCurrentDisease(patientId){
        return await this.get("/list-current-disease-full", {"patient-id": patientId});
    }

    async listDisease(patientId){
        return await this.get("/list-disease-full", {"patient-id": patientId});
    }

    async searchByoumeiMaster(text, at){
        return await this.get("/search-byoumei-master", {text, at});
    }

    async searchShuushokugoMaster(text, at){
        return await this.get("/search-shuushokugo-master", {text, at});
    }

    async enterDisease(req){
        return await this.post("/enter-disease", req);
    }

    async getDisease(diseaseId){
        return await this.get("/get-disease-full", {"disease-id": diseaseId});
    }

    async listDiseaseExample(){
        return await this.get("/list-disease-example", {});
    }

    async findByoumeiMasterByName(name, at){
        return await this.get("/find-byoumei-master-by-name", {name, at});
    }

    async findShuushokugoMasterByName(name){
        return await this.get("/find-shuushokugo-master-by-name", {name});
    }

    async batchUpdateDiseaseEndReason(reqs){
        return await this.post("/batch-update-disease-end-reason", reqs);
    }

    async modifyDisease(req){
        return await this.post("/modify-disease", req);
    }

    async deleteDisease(diseaseId){
        return await this.get("/delete-disease", {"disease-id": diseaseId});
    }

    async searchText(patientId, text, page){
        return await this.get("/search-text-by-page", {
            "patient-id": patientId,
            text,
            page
        });
    }

    async searchTextGlobally(text, page){
        return await this.get("/search-text-globally", {text, page});
    }

    async listRecentlyRegisteredPatients(n = 20){
        return await this.get("/list-recently-registered-patients", {n});
    }

    async listAvailableHoken(patientId, date){
        return await this.get("/list-available-hoken", {"patient-id": patientId, at: date});
    }

    async listAllHoken(patientId){
        let result = await this.get("/list-hoken", {"patient-id": patientId});
        return {
            shahokokuhoList: result.shahokokuhoListDTO,
            koukikoureiList: result.koukikoureiListDTO,
            roujinList: result.roujinListDTO,
            kouhiList: result.kouhiListDTO,
        };
    }

    async enterShahokokuho(shahokokuho){
        return await this.post("/enter-shahokokuho", shahokokuho);
    }

    async enterKoukikourei(koukikourei){
        return await this.post("/enter-koukikourei", koukikourei);
    }

    async enterRoujin(roujin){
        return await this.post("/enter-roujin", roujin);
    }

    async enterKouhi(kouhi){
        return await this.post("/enter-kouhi", kouhi);
    }

    async getShahokokuho(shahokokuhoId){
        return await this.get("/get-shahokokuho", {"shahokokuho-id": shahokokuhoId});
    }

    async getKoukikourei(koukikoureiId){
        return await this.get("/get-koukikourei", {"koukikourei-id": koukikoureiId});
    }

    async getRoujin(roujinId){
        return await this.get("/get-roujin", {"roujin-id": roujinId});
    }

    async getKouhi(kouhiId){
        return await this.get("/get-kouhi", {"kouhi-id": kouhiId});
    }

    async deleteShahokokuho(shahokokuho){
        return await this.post("/delete-shahokokuho", shahokokuho);
    }

    async deleteKoukikourei(koukikourei){
        return await this.post("/delete-koukikourei", koukikourei);
    }

    async deleteRoujin(roujin){
        return await this.post("/delete-roujin", roujin);
    }

    async deleteKouhi(kouhi){
        return await this.post("/delete-kouhi", kouhi);
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



