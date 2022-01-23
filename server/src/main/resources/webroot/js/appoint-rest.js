import {HttpClient} from "./http-client.js";

export class AppointRest extends HttpClient {
    constructor() {
        super("/appoint");
    }

    async listAppointTime(from, upto){
        return this.GET("/list-appoint-time", {from, upto});
    }
}