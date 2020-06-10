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
        this.pdfFileElement.text(getFilePart(pdfFile));
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