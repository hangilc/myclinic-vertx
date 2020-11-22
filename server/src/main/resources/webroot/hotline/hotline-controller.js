import {nowAsSqldatetime} from "../js/kanjidate.js";
import {createElementFrom} from "../js/create-element-from.js";

export class HotlineController {
    constructor(name, peer, listElement, rest){
        this.name = name;
        this.peer = peer;
        this.listElement = listElement;
        this.rest = rest;
        this.ws = new WebSocket(`ws://${window.location.host}/hotline`);
        this.ws.addEventListener("message", async event => await this.doMessage(event.data));
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

    async doMessage(message){
        let log = JSON.parse(message);
        if( log.kind === "created" ){
            let body = JSON.parse(log.body);
            console.log(body.created);
            let e = createMessageDisp(body.created);
            this.listElement.prepend(e);
        } else if( log.kind === "beep" ){
            await this.doBeep();
        }
    }

    async doBeep(){
        console.log("BEEP");
    }
}

let messageTmpl = `
<div></div>
`;

function createMessageDisp(hotline){
    let e = createElementFrom(messageTmpl);
    e.innerText = hotline.message;
    return e;
}