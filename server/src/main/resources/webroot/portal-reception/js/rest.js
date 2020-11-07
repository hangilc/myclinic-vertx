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
}