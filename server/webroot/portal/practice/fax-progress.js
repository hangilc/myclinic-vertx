import {Component} from "./component.js";

export class FaxProgress extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.patientNameElement = map.patientName;
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

    async poll() {
        let status = await this.rest.pollFax(this.faxSid);
        this.addMessage(status);
        if (status === "sending" || status === "processing" || status === "queued") {
            setTimeout(() => this.poll(), 10000);
        }
    }

    view(){
        let url = rest.url("/show-pdf", {file: this.pdfFile});
        window.open(url, "_blank");
    }

    init(patientName, faxNumber, pdfFile, faxSid) {
        this.pdfFile = pdfFile;
        this.patientNameElement.text(patientName);
        this.faxNumberElement.text(faxNumber);
        this.pdfFileElement.text(pdfFile);
        this.faxSid = faxSid;
        this.closeElement.on("click", event => this.remove());
        this.viewElement.on("click", event => this.view());
    }

    start() {
        this.addMessage("started");
        setTimeout(async () => {
            await this.poll();
        }, 10000);
    }
}