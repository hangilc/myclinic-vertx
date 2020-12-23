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
                //alert(msg);
                console.error(msg);
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
                //alert(msg);
                console.error(msg);
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
    constructor(baseUrl, topRest) {
        super(baseUrl);
        this.topRest = topRest;
    }

    async listWqueueFullForExam() {
        return await this.get("/list-wqueue-full-for-exam", {});
    }

    async listWqueueFull() {
        return await ajaxGet(this.url("/list-wqueue-full"), {});
    }

    async getMeisai(visitId) {
        return await ajaxGet(this.url("/get-visit-meisai"), {"visit-id": visitId});
    }

    async endExam(visitId, charge) {
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

    async modifyCharge(visitId, amount) {
        return await this.get("/modify-charge", {"visit-id": visitId, charge: amount});
    }

    async getCharge(visitId) {
        return await this.get("/get-charge", {"visit-id": visitId});
    }

    async listPayment(visitId) {
        return await this.get("/list-payment", {"visit-id": visitId});
    }

    async getClinicInfo() {
        return await this.get("/get-clinic-info", {});
    }

    async searchPatient(text) {
        return await ajaxGet(this.url("/search-patient"), {text: text});
    }

    async enterPatient(patient) {
        return await this.post("/enter-patient", patient);
    }

    async updatePatient(patient) {
        return await this.post("/update-patient", patient);
    }

    async startVisit(patientId) {
        return await ajaxGet(this.url("/start-visit"), {"patient-id": patientId});
    }

    async listVisit(patientId, page) {
        return await ajaxGet(this.url("/list-visit-full2"), {"patient-id": patientId, page: page});
    }

    async listPaymentVisitByPatient(patientId) {
        return await this.get("/list-payment-visit-by-patient", {"patient-id": patientId});
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
        return await this.get("/list-text", {"visit-id": visitId});
    }

    async deleteText(textId) {
        return await this.post("/delete-text", {}, {
            params: {
                "text-id": textId
            }
        });
    }

    async getHoken(visitId) {
        return await this.get("/get-hoken", {"visit-id": visitId});
    }

    async hokenRep(hoken) {
        let rep = await ajaxPost(this.url("/hoken-rep"), hoken);
        if (!rep) {
            rep = "［保険なし］";
        }
        return rep;
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

    async getVisit(visitId) {
        return await this.get("/get-visit", {"visit-id": visitId});
    }

    async updateVisitAttr(visitId, attr) {
        return await this.get("/update-visit-attr", {"visit-id": visitId, attr});
    }

    async deleteVisit(visitId) {
        return await this.post("/delete-visit", {}, {
            params: {"visit-id": visitId}
        });
    }

    async deleteVisitFromReception(visitId) {
        return await this.get("/delete-visit-from-reception", {"visit-id": visitId});
    }

    async getPatient(patientId) {
        return await this.get("/get-patient", {"patient-id": patientId});
    }

    async calcRcptAge(birthday, at) {
        return await this.get("/calc-rcpt-age", {"birthday": birthday, "at": at});
    }

    async calcFutanWari(hoken, rcptAge, visit) {
        let req = {
            hoken: hoken,
            rcptAge: rcptAge,
            visit: visit
        };
        return await this.post("/calc-futan-wari", req);
    }

    async printDrawer(pages, setting = null) { // setting is optional
        let params = {};
        if (setting) {
            params.setting = setting;
        }
        return await this.post("/print-drawer", pages, {
            params: params
        });
    }

    async saveDrawerAsPdf(pages, paperSize, savePath, ops) {
        let req = {
            pages,
            paperSize,
            savePath
        }
        Object.assign(req, ops);
        return await this.post("/save-drawer-as-pdf", req);
    }

    async shohousenDrawer(shohousenRequest) {
        return await this.post("/shohousen-drawer", shohousenRequest);
    }

    async shohousenGrayStampInfo() {
        return await this.get("/shohousen-gray-stamp-info");
    }

    async convertToRomaji(text) {
        let result = await this.get("/convert-to-romaji", {text: text});
        return result.value;
    }

    async getShohousenSavePdfPath(name, textId, patientId, date) {
        return await this.get("/get-shohousen-save-pdf-path", {
            "name": name,
            "text-id": textId,
            "patient-id": patientId,
            "date": date
        });
    }

    // async saveShohousenPdf(shohousenRequest, textId){
    //     return await this.post("/save-shohousen-pdf", shohousenRequest,
    //         { params: {"text-id": textId} });
    // }

    async suspendExam(visitId) {
        return await this.get("/suspend-exam", {"visit-id": visitId});
    }

    async startExam(visitId) {
        return await this.get("/start-exam", {"visit-id": visitId});
    }

    async listRecentVisitWithPatient(page) {
        if (page == null) {
            page = 0;
        }
        return await this.get("/list-recent-visit-with-patient", {page: page});
    }

    async listTodaysVisits() {
        return await this.get("/list-todays-visits", {});
    }

    async listVisitPatientAt(at) {
        return await this.get("/list-visit-patient-at", {at: at});
    }

    async batchEnterShinryouByNames(names, visitId) {
        return await this.get("/batch-enter-shinryou-by-name", {
            name: names,
            "visit-id": visitId
        });
    }

    async batchCopyShinryou(targetVisitId, shinryouList) {
        return await this.post("/batch-copy-shinryou", shinryouList, {
            "params": {
                "visit-id": targetVisitId
            }
        });
    }

    async listShinryou(visitId) {
        return await this.get("/list-shinryou", {"visit-id": visitId});
    }

    async listShinryouFullByIds(shinryouIds) {
        if (shinryouIds.length === 0) {
            return [];
        }
        return await this.get("/list-shinryou-full-by-ids", {"shinryou-id": shinryouIds});
    }

    async getShinryouFull(shinryouId) {
        return await this.get("/get-shinryou-full", {"shinryou-id": shinryouId});
    }

    async enterShinryou(shinryou) {
        return await this.post("/enter-shinryou", shinryou);
    }

    async deleteShinryou(shinryouId) {
        return await this.get("/delete-shinryou", {"shinryou-id": shinryouId});
    }

    async searchShinryouMaster(text, at) {
        return await this.get("/search-shinryou-master", {text, at});
    }

    async listDrugFullByIds(drugIds) {
        if (drugIds.length === 0) {
            return [];
        }
        return await this.get("/list-drug-full-by-drug-ids", {"drug-id": drugIds});
    }

    async listConductFullByIds(conductIds) {
        if (conductIds.length === 0) {
            return [];
        }
        return await this.get("/list-conduct-full-by-ids", {"conduct-id": conductIds});
    }

    async probeShohousenFaxImage(textId, date) {
        return await this.get("/probe-shohousen-fax-image", {"text-id": textId, "date": date});
    }

    async sendFax(faxNumber, pdfFile) {
        return await this.get("/send-fax", {"fax-number": faxNumber, "pdf-file": pdfFile});
    }

    async pollFax(faxSid) {
        return await this.get("/poll-fax", {"fax-sid": faxSid});
    }

    async listCurrentDisease(patientId) {
        return await this.get("/list-current-disease-full", {"patient-id": patientId});
    }

    async listDisease(patientId) {
        return await this.get("/list-disease-full", {"patient-id": patientId});
    }

    async searchByoumeiMaster(text, at) {
        return await this.get("/search-byoumei-master", {text, at});
    }

    async searchShuushokugoMaster(text, at) {
        return await this.get("/search-shuushokugo-master", {text, at});
    }

    async enterDisease(req) {
        return await this.post("/enter-disease", req);
    }

    async getDisease(diseaseId) {
        return await this.get("/get-disease-full", {"disease-id": diseaseId});
    }

    async listDiseaseExample() {
        return await this.get("/list-disease-example", {});
    }

    async findByoumeiMasterByName(name, at) {
        return await this.get("/find-byoumei-master-by-name", {name, at});
    }

    async findShuushokugoMasterByName(name) {
        return await this.get("/find-shuushokugo-master-by-name", {name});
    }

    async getByoumeiMaster(shoubyoumeicode, at) {
        return await this.get("/get-byoumei-master", {shoubyoumeicode, at});
    }

    async getShuushokugoMaster(shuushokugocode, at) {
        return await this.get("/get-shuushokugo-master", {shuushokugocode, at});
    }

    async batchUpdateDiseaseEndReason(reqs) {
        return await this.post("/batch-update-disease-end-reason", reqs);
    }

    async modifyDisease(req) {
        return await this.post("/modify-disease", req);
    }

    async deleteDisease(diseaseId) {
        return await this.get("/delete-disease", {"disease-id": diseaseId});
    }

    async searchText(patientId, text, page) {
        return await this.get("/search-text-by-page", {
            "patient-id": patientId,
            text,
            page
        });
    }

    async searchTextGlobally(text, page) {
        return await this.get("/search-text-globally", {text, page});
    }

    async listRecentlyRegisteredPatients(n = 20) {
        return await this.get("/list-recently-registered-patients", {n});
    }

    async listAvailableHoken(patientId, date) {
        return await this.get("/list-available-hoken", {"patient-id": patientId, at: date});
    }

    async listAvailableAllHoken(patientId, date) {
        return await this.get("/list-available-all-hoken", {"patient-id": patientId, at: date});
    }

    async listAllHoken(patientId) {
        let result = await this.get("/list-hoken", {"patient-id": patientId});
        return {
            shahokokuhoList: result.shahokokuhoList,
            koukikoureiList: result.koukikoureiList,
            roujinList: result.roujinList,
            kouhiList: result.kouhiList,
        };
    }

    async enterShahokokuho(shahokokuho) {
        return await this.post("/enter-shahokokuho", shahokokuho);
    }

    async updateShahokokuho(shahokokuho) {
        return await this.post("/update-shahokokuho", shahokokuho);
    }

    async enterKoukikourei(koukikourei) {
        return await this.post("/enter-koukikourei", koukikourei);
    }

    async updateKoukikourei(koukikourei) {
        return await this.post("/update-koukikourei", koukikourei);
    }

    async enterRoujin(roujin) {
        return await this.post("/enter-roujin", roujin);
    }

    async updateRoujin(roujin) {
        return await this.post("/update-roujin", roujin);
    }

    async enterKouhi(kouhi) {
        return await this.post("/enter-kouhi", kouhi);
    }

    async updateKouhi(kouhi) {
        return await this.post("/update-kouhi", kouhi);
    }

    async getShahokokuho(shahokokuhoId) {
        return await this.get("/get-shahokokuho", {"shahokokuho-id": shahokokuhoId});
    }

    async getKoukikourei(koukikoureiId) {
        return await this.get("/get-koukikourei", {"koukikourei-id": koukikoureiId});
    }

    async getRoujin(roujinId) {
        return await this.get("/get-roujin", {"roujin-id": roujinId});
    }

    async getKouhi(kouhiId) {
        return await this.get("/get-kouhi", {"kouhi-id": kouhiId});
    }

    async deleteShahokokuho(shahokokuho) {
        return await this.post("/delete-shahokokuho", shahokokuho);
    }

    async deleteKoukikourei(koukikourei) {
        return await this.post("/delete-koukikourei", koukikourei);
    }

    async deleteRoujin(roujin) {
        return await this.post("/delete-roujin", roujin);
    }

    async deleteKouhi(kouhi) {
        return await this.post("/delete-kouhi", kouhi);
    }

    async updateHoken(visit) {
        return await this.post("/update-hoken", visit);
    }

    async listShujiiPatient() {
        return await this.get("/list-shujii-patient");
    }

    async batchGetPatient(patientIds) {
        return await this.post("/batch-get-patient", patientIds);
    }

    async getShujiiMasterText(patient) {
        return await this.post("/get-shujii-master-text", patient);
    }

    async saveShujiiMasterText(patientName, patientId, text) {
        return await this.post("/save-shujii-master-text", text, {
            params: {name: patientName, "patient-id": patientId}
        });
    }

    // async compileShujiiDrawer(shujiiData, setting) {
    //     return await this.post("/compile-shujii-drawer", shujiiData, {
    //         params: {setting}
    //     });
    // }

    async compileShujiiDrawer(shujiiData) {
        return await this.post("/compile-shujii-drawer", shujiiData);
    }

    async listPrinterSetting() {
        return await this.get("/list-printer-setting");
    }

    async createPrinterSetting(setting) {
        return await this.get("/create-printer-setting", {setting});
    }

    async modifyPrinterSetting(setting) {
        return await this.get("/modify-printer-setting", {setting});
    }

    async printGuideFrame(paper, setting, inset) { // all params are optional
        return await this.get("/print-guide-frame", {paper, setting, inset});
    }

    async getPrinterJsonSetting(setting) {
        return await this.get("/get-printer-json-setting", {setting});
    }

    async savePrinterJsonSetting(setting, jsonSetting) {
        return await this.post("/save-printer-json-setting", jsonSetting, {
            params: {setting}
        });
    }

    async printRefer(data) {
        return await this.post("/print-refer", data);
    }

    async saveRefer(data, patientId) {
        return await this.post("/save-refer", data, {
            params: {"patient-id": patientId}
        });
    }

    async listRefer(patientId) {
        return await this.get("/list-refer", {"patient-id": patientId});
    }

    async getRefer(patientId, file) {
        return await this.get("/get-refer", {"patient-id": patientId, "file": file});
    }

    async deleteRefer(patientId, file) {
        return await this.get("/delete-refer", {"patient-id": patientId, "file": file});
    }

    async getReferList() {
        return await this.get("/get-refer-list");
    }

    async moveAppFile(src, dst) {
        return await this.get("/move-app-file", {src, dst});
    }

    async deleteAppFile(file) {
        return await this.get("/delete-app-file", {file});
    }

    async searchPrescExample(text) {
        return await this.get("/search-presc-example-full-by-name", {text});
    }

    async resolveIyakuhinMaster(iyakuhincode, at) {
        return await this.get("/resolve-iyakuhin-master", {iyakuhincode, at});
    }

    async createTempFileName(prefix, suffix) {
        return await this.get("/create-temp-file-name", {prefix, suffix});
    }

    async deleteFile(file) {
        return await this.get("/delete-file", {file});
    }

    async copyFile(src, dst, mkdir) {  // mkdir (optional: boolean): whether create directories if necessary
        let param = {src, dst};
        if (mkdir) {
            param.mkdir = true;
        }
        return await this.get("/copy-file", param);
    }

    async createReferImageSavePath(patientId, suffix) {
        return await this.get("/create-refer-image-save-path", {"patient-id": patientId, suffix});
    }

    async referStampInfo() {
        return await this.get("/refer-stamp-info");
    }

    async putStampOnPdf(srcFile, stamp, dstFile) {
        return await this.get("/put-stamp-on-pdf", {
            "src-file": srcFile,
            "stamp": stamp,
            "dst-file": dstFile
        });
    }

    async renderMedCert(medCertData) {
        return await this.post("/render-medcert", medCertData);
    }

    async setShinryouTekiyou(shinryouId, tekiyouText) {
        return await this.get("/set-shinryou-tekiyou", {
            "shinryou-id": shinryouId,
            "tekiyou": tekiyouText
        });
    }

    async getMostRecentVisitOfPatient(patientId) {
        return await this.get("/get-most-recent-visit-of-patient", {"patient-id": patientId});
    }

    async createPaperScanPath(patientId, fileName) {
        return await this.get("/create-paper-scan-path", {
            "patient-id": patientId,
            "file-name": fileName
        });
    }

    async receiptDrawer(req) {
        return await this.post("/receipt-drawer", req);
    }

    async listPrintSetting() {
        return await this.get("/list-print-setting", {});
    }

    async listRecentPayment() {
        return await this.get("/list-recent-payment", {});
    }

    async enterHotline(hotline) {
        return await this.post("/enter-hotline", hotline);
    }

    async sendHotlineBeep(target) {
        return await this.get("/send-hotline-beep", {target});
    }

    async listTodaysHotline() {
        return await this.get("/list-todays-hotline", {});
    }

    async listHotlineAfter(hotlineId) {
        return await this.get("/list-recent-hotline", {"threshold-hotline-id": hotlineId});
    }

    async listTodaysHotlineInRange(after, before) {
        return await this.get("/list-todays-hotline-in-range", {after, before});
    }

    async getDrugFull(drugId) {
        return await this.get("/get-drug-full", {"drug-id": drugId});
    }

    async getConductFull(conductId) {
        return await this.get("/get-conduct-full", {"conduct-id": conductId});
    }

    async listPatientImage(patientId) {
        return await this.topRest.listPatientImage(patientId);
    }

    async getPatientImageBlob(patientId, file) {
        return await this.topRest.getPatientImageBlob(patientId, file);
    }

    urlOfPatientImage(patientId, file) {
        return this.topRest.urlOfPatientImage(patientId, file);
    }

    async changePatientOfImage(srcPatientId, srcFile, dstPatientId, dstFile) {
        return await this.topRest.changePatientOfImage(srcPatientId, srcFile, dstPatientId, dstFile);
    }

    async savedPatientImageToken(patientId, file) {
        return await this.topRest.savedPatientImageToken(patientId, file);
    }

    urlViewDrawerAsPdf(){
        return this.topRest.urlViewDrawerAsPdf();
    }

    urlShowFileToken(fileToken){
        return this.topRest.urlShowFileToken(fileToken);
    }

    async createReceiptPdf(visitIds){
        return await this.topRest.createReceiptPdf(visitIds);
    }

    async batchEnterPayment(payments){
        return await this.topRest.batchEnterPayment(payments);
    }

    async getLastPayment(visitId){
        return this.topRest.getLastPayment(visitId);
    }

    async batchGetLastPayment(visitIds){
        return await this.topRest.batchGetLastPayment(visitIds);
    }

    async list0410NoPay(patientId){
        return await this.topRest.list0410NoPay(patientId);
    }

    async batchGetVisit(visitIds){
        return await this.topRest.batchGetVisit(visitIds);
    }
}

class Integration extends Client {
    constructor(baseUrl) {
        super(baseUrl);
    }

    async createShijisho(data) {
        return await this.post("/houmon-kango/create-shijisho", data);
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



