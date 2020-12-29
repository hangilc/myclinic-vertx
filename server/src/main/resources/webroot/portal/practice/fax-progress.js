import {Component} from "./component.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";

function getFilePart(path){
    let i = path.lastIndexOf("\\");
    if( i >= 0 ){
        return path.substring(i+1);
    }
    i = path.lastIndexOf("/");
    if( i >= 0 ){
        return path.substring(i+1);
    }
    return path;
}

let tmpl = `
    <div class="border founded mb-3 p-2">
        <div class="x-title"></div>
        <div class="mt-2">
            <span class="x-pdf-file"></span>
            <a href="javascript:void(0)" class="x-view">表示</a>
        </div>
        <div class="x-fax-number"></div>
        <div class="x-message mt-4"></div>
        <div class="mt-4">
            <button class="btn btn-secondary x-re-send">再送信</button>
            <a href="javascript:void(0)" class="x-close ml-2">閉じる</a>
        </div>
    </div>
`;

export class FaxProgress {
    constructor(rest, title, faxNumber, pdfFile, faxSid){
        this.rest = rest;
        this.faxNumber = faxNumber;
        this.pdfFile = pdfFile;
        this.faxSid = faxSid;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.title.innerText = title;
        this.map.faxNumber.innerText = faxNumber;
        this.map.pdfFile.innerText = getFilePart(pdfFile);
        this.map.view.addEventListener("click", event => {
            let url = this.rest.url("/show-pdf", {file: pdfFile});
            window.open(url, "_blank");
        });
        this.map.reSend.addEventListener("click", async event => await this.doReSend());
        this.map.close.addEventListener("click", event => this.ele.remove());
    }

    start() {
        this.addMessage("started");
        this.startPoll();
    }

    addMessage(msg) {
        let e = document.createElement("div");
        e.innerText = msg;
        this.map.message.append(e);
    }

    clearMessage(){
        this.map.message.innerHTML = "";
    }

    async poll() {
        let status = await this.rest.pollFax(this.faxSid);
        this.addMessage(status);
        if (status === "sending" || status === "processing" || status === "queued") {
            this.startPoll();
        }
    }

    startPoll(){
        setTimeout(async () => {
            await this.poll();
        }, 10000);
    }

    async doReSend(){
        this.faxSid = await this.rest.sendFax(this.faxNumber, this.pdfFile);
        this.clearMessage();
        this.addMessage("restarted");
        this.startPoll();
    }

}

class FaxProgressOrig extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.titleElement = map.title;
        this.faxNumberElement = map.faxNumber;
        this.pdfFileElement = map.pdfFile;
        this.viewElement = map.view;
        this.messageElement = map.message;
        this.reSendElement = map.reSend;
        this.closeElement = map.close;
    }

    addMessage(msg) {
        let e = $("<div>").text(msg);
        this.messageElement.append(e);
    }

    clearMessage(){
        this.messageElement.html("");
    }

    async poll() {
        let status = await this.rest.pollFax(this.faxSid);
        this.addMessage(status);
        if (status === "sending" || status === "processing" || status === "queued") {
            setTimeout(() => this.poll(), 10000);
        }
    }

    startPoll(){
        setTimeout(async () => {
            await this.poll();
        }, 10000);
    }

    view(){
        let url = rest.url("/show-pdf", {file: this.pdfFile});
        window.open(url, "_blank");
    }

    init(title, faxNumber, pdfFile, faxSid) {
        this.pdfFile = pdfFile;
        this.faxNumber = faxNumber;
        this.titleElement.text(title);
        this.setFaxNumberDisp(faxNumber);
        this.setPdfFileDisp(pdfFile);
        this.faxSid = faxSid;
        this.closeElement.on("click", event => this.remove());
        this.viewElement.on("click", event => this.view());
        this.reSendElement.on("click", event => this.resend());
    }

    setFaxNumberDisp(faxNumber){
        this.faxNumberElement.text(faxNumber);
    }

    setPdfFileDisp(pdfFile){
        this.pdfFileElement.text(getFilePart(pdfFile));
    }

    async resend(){
        this.faxSid = await this.rest.sendFax(this.faxNumber, this.pdfFile);
        this.clearMessage();
        this.addMessage("restarted");
        this.startPoll();
    }

    start() {
        this.addMessage("started");
        this.startPoll();
    }
}