import {nowAsSqldatetime} from "../js/kanjidate.js";

export class HotlineController {
    constructor(name, peer, listElement, rest){
        this.name = name;
        this.peer = peer;
        this.listElement = listElement;
        this.rest = rest;
    }

    async submit(message){
        let postedAt = nowAsSqldatetime();
        let hotline = {
            message,
            sender: this.name,
            recipient: this.peer,
            postedAt
        }
        return await this.rest.enterHotline(hotline);
    }
}