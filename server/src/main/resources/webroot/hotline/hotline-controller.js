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
        this.lastHotlineId = 0;
        this.timer = setInterval(async () => this.doCheckMessages(), 20000); // every 20 seconds
    }

    async init(){
        let hotlines = await this.rest.listTodaysHotline();
        for(let hotline of hotlines){
            this.addMessage(hotline);
        }
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

    addMessage(message){
        if( this.lastHotlineId !== 0 && (this.lastHotlineId + 1) !== message.hotlineId ){
            console.error("Missing hotline", this.lastHotlineId, message.hotlineId);
        }
        if( message.hotlineId > this.lastHotlineId ){
            let e = createMessageDisp(message);
            this.listElement.prepend(e);
            this.listElement.scrollTop = 0;
            this.lastHotlineId = message.hotlineId;
        }
    }

    async fillGapMessages(afer, before){
        let gaps = await this.rest.listTodaysHotlineInRange(after, before);
        for(let hotline of gaps){
            this.addMessage(hotline);
        }
    }

    async doCheckMessages(){
        if( this.lastHotlineId > 0 ){
            let messages = await this.rest.listHotlineAfter(this.lastHotlineId);
            for(let m of messages){
                this.addMessage(m);
            }
        } else {
            let messages = await this.rest.listTodaysHotline();
            for(let m of messages){
                this.addMessage(m);
            }
        }
    }

    async doMessage(message){
        let log = JSON.parse(message);
        if( log.kind === "created" ){
            let hotline = JSON.parse(log.body).created;
            if( hotline.hotlineId > this.lastHotlineId && this.isMyMessage(hotline) ){
                if( (this.lastHotlineId + 1) < hotline.hotlineId ){
                    await this.fillGapMessages(this.lastHotlineId, hotline.hotlineId);
                }
                this.addMessage(hotline);
            }
        } else if( log.kind === "beep" ){
            await this.doBeep();
        }
    }

    async doBeep(){
        console.log("BEEP");
    }

    isMyMessage(hotline){
        let sender = hotline.sender;
        let recipient = hotline.recipient;
        return (sender === this.name && recipient === this.peer) ||
            (sender === this.peer && recipient === this.name)
    }
}

let nameMap = {
    practice: "診察",
    reception: "受付"
}

function nameRep(name){
    return nameMap[name] || name;
}

let messageTmpl = `
<div></div>
`;

function createMessageDisp(hotline){
    let e = createElementFrom(messageTmpl);
    let rep = nameRep(hotline.sender);
    e.innerText = `${rep}> ${hotline.message}`;
    return e;
}

