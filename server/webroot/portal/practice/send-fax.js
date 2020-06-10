import {Component} from "./component.js";

export class SendFax extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.pdfFileElement = map.pdfFile;
        this.viewElement = map.view;
        this.faxNumberElement = map.faxNumber;
        this.sendElement = map.send;
        this.cancelElement = map.cancel;
    }

    init(pdfFile, faxNumber){
        this.pdfFile = pdfFile;
        this.faxNumber = faxNumber;
        this.pdfFileElement.text(pdfFile);
        this.faxNumberElement.text(faxNumber);
        this.sendElement.on("click", event => this.doSend());
        this.viewElement.on("click", event => this.doView());
        this.cancelElement.on("click", event => this.doCancel());
    }

    doCancel(){
        this.remove();
    }

    doView(){
        let url = rest.url("/show-pdf", {file: this.pdfFile});
        window.open(url, "_blank");

    }

    async doSend(){
        let faxSid = await this.rest.sendFax(this.faxNumber, this.pdfFile);
        this.remove();
        this.trigger("sent", faxSid);
    }

    onSent(cb){
        this.on("sent", (event, faxSid) => cb(event, faxSid));
    }
}