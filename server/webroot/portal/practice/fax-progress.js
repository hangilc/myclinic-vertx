import {Component} from "./component.js";

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

export class FaxProgress extends Component {
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