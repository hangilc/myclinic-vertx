import {HttpClient} from "./http-client.js";

export class Rest extends HttpClient {
    constructor(url){
        super(url);
    }

    async enterPatient(patient){
        return await this.POST("/enter-patient", patient);
    }

    async getPatient(patientId){
        return await this.GET("/get-patient", {"patient-id": patientId});
    }

    async updatePatient(patient){
        return await this.POST("/update-patient", patient);
    }

    async searchPatient(text){
        return await this.GET("/search-patient", {text});
    }

    async listAvailableAllHoken(patientId, at){ // available kouhi is reported as list
        return await this.GET("/list-available-all-hoken", {"patient-id": patientId, at});
    }

    async listAvailableHoken(patientId, at){ // available kouhi is reported as kouhi_1, ...
        return await this.GET("/list-available-hoken", {"patient-id": patientId, at});
    }

    async listAllHoken(patientId){
        return await this.GET("/list-hoken", {"patient-id": patientId});
    }

    async batchResolveHokenRep(hokenList){
        return await this.POST("/batch-resolve-hoken-rep", hokenList);
    }

    async enterShahokokuho(shahokokuho){
        return await this.POST("/enter-shahokokuho", shahokokuho);
    }

    async updateShahokokuho(shahokokuho){
        return await this.POST("/update-shahokokuho", shahokokuho);
    }

    async enterKoukikourei(koukikourei){
        return await this.POST("/enter-koukikourei", koukikourei);
    }

    async updateKoukikourei(koukikourei){
        return await this.POST("/update-koukikourei", koukikourei);
    }

    async enterKouhi(kouhi){
        return await this.POST("/enter-kouhi", kouhi);
    }

    async updateKouhi(kouhi){
        return await this.POST("/update-kouhi", kouhi);
    }

    async getVisit(visitId){
        return await this.GET("/get-visit", {"visit-id": visitId});
    }

    async startVisit(patientId){ // returns visit-id
        return await this.GET("/start-visit", {"patient-id": patientId});
    }

    async deleteVisitFromReception(visitId){
        return await this.GET("/delete-visit-from-reception", {"visit-id": visitId});
    }

    async listWqueueFull(){
        return await this.GET("/list-wqueue-full", {});
    }

    async getMeisai(visitId){
        return await this.GET("/get-visit-meisai", {"visit-id": visitId});
    }

    async getCharge(visitId){
        return await this.GET("/get-charge", {"visit-id": visitId});
    }

    async finishCharge(visitId, amount, payTime) {
        // if (moment.isMoment(payTime)) {
        //     payTime = payTime.format("YYYY-MM-DD HH:mm:ss");
        // }
        if (typeof payTime !== "string") {
            throw `Invalid paytime: ${payTime}`;
        }
        let dto = {
            visitId: visitId,
            amount: amount,
            paytime: payTime
        }
        return await this.POST("/finish-cashier", dto);
    }

    async getClinicInfo(){
        return await this.GET("/get-clinic-info", {});
    }

    async receiptDrawer(req){
        return await this.POST("/receipt-drawer", req);
    }

    async savePatientImageBlob(patientId, blobParts, filename){
        return await this.uploadFileBlob("/save-patient-image",
            blobParts,
            filename,
            {"patient-id": patientId});
    }

    async deletePatientImage(patientId, file){
        return await this.GET("/delete-patient-image", {"patient-id": patientId, file})
    }

    async listPatientImage(patientId){
        return await this.GET("/list-patient-image", {"patient-id": patientId});
    }

    urlOfPatientImage(patientId, file){
        return this.composeUrl("/get-patient-image", {"patient-id": patientId, file});
    }

    async getPatientImageBlob(patientId, file){
        return await this.downloadFileBlob("/get-patient-image", {"patient-id": patientId, file})
    }

    async changePatientOfImage(srcPatientId, srcFile, dstPatientId, dstFile){
        return await this.GET("/change-patient-of-image", {
            "src-patient-id": srcPatientId,
            "src-file": srcFile,
            "dst-patient-id": dstPatientId,
            "dst-file": dstFile
        });
    }

    async savedPatientImageToken(patientId, file){
        return await this.GET("/saved-patient-image-token", {"patient-id": patientId, file});
    }

    async enterHotline(hotline){
        return await this.POST("/enter-hotline", hotline);
    }

    async sendHotlineBeep(target){
        return await this.GET("/send-hotline-beep", {target});
    }

    async listTodaysHotline(){
        return await this.GET("/list-todays-hotline", {});
    }

    async listHotlineAfter(hotlineId){
        return await this.GET("/list-recent-hotline", {"threshold-hotline-id": hotlineId});
    }

    async listTodaysHotlineInRange(after, before){
        return await this.GET("/list-todays-hotline-in-range", {after, before});
    }

    urlViewDrawerAsPdf(){
        return this.composeUrl("/view-drawer-as-pdf");
    }

    urlShowFileToken(fileToken){
        return this.composeUrl("/show-file-token", {"file": fileToken});
    }

    async createReceiptPdf(visitIds){
        return await this.POST("/create-receipt-pdf", visitIds, {});
    }

    async batchEnterPayment(payments){
        return await this.POST("/batch-enter-payment", payments);
    }

    async getLastPayment(visitId){
        let map = await this.batchGetLastPayment([visitId]);
        return map[visitId];
    }

    async batchGetLastPayment(visitIds){
        return await this.POST("/batch-get-last-payment", visitIds, {});
    }

    async list0410NoPay(patientId){
        return await this.GET("/list-0410-no-pay", {"patient-id": patientId});
    }

    async batchGetVisit(visitIds){
        return await this.POST("/batch-get-visit", visitIds, {});
    }

    async deleteDuplicateShinryou(visitId){
        return await this.GET("/delete-duplicate-shinryou", {"visit-id": visitId});
    }

    async enterXp(visitId, label, film){
        return await this.GET("/enter-xp", {"visit-id": visitId, label, film});
    }

    async enterInjection(visitId, kind, iyakuhincode, amount){
        return await this.GET("/enter-inject", {"visit-id": visitId, kind, iyakuhincode, amount});
    }

    async deleteConduct(conductId){
        return await this.GET("/delete-conduct", {"conduct-id": conductId});
    }

    async searchIyakuhinMaster(text, at){
        return await this.GET("/search-iyakuhin-master-by-name", {text, at});
    }

    async batchGetPatient(patients){
        return await this.POST("/batch-get-patient", patients, {});
    }

    async copyAllConducts(srcVisitId, targetVisitId){
        return await this.GET("/copy-all-conducts", {"source-visit-id": srcVisitId, "target-visit-id": targetVisitId})
    }

    async listConductFullByIds(conductIds){
        return await this.POST("/list-conduct-full-by-ids", conductIds, {});
    }

    async getMostRecentVisitOfPatient(patientId){
        return this.GET("/get-most-recent-visit-of-patient", {"patient-id": patientId});
    }

    async listVisit(patientId, page) {
        return this.GET("/list-visit-full2", {"patient-id": patientId, page: page});
    }

}