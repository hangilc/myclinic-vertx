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

    async listWqueueFull(){
        return await this.GET("/list-wqueue-full", {});
    }

    async getMeisai(visitId){
        return await this.GET("/get-visit-meisai", {"visit-id": visitId});
    }

    async getCharge(visitId){
        return await this.GET("/get-charge", {"visit-id": visitId});
    }

}