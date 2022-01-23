import {HttpClient} from "./http-client.js";

export class Api extends HttpClient {
    constructor(url){
        super(url);
    }

    async getTwilioToken() {
        return await this.GET("/twilio-token");
    }
}