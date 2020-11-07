import {HttpClient} from "./http-client.js";

export class Rest extends HttpClient {
    constructor(url){
        super(url);
    }

    async enterPatient(patient){
        return await this.POST("/enter-patient", patient);
    }
}