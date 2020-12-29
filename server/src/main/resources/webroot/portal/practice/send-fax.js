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
        <div class="mt-2">
            <span class="x-pdf-file"></span>
            <a href="javascript:void(0)" class="x-view">プレビュー</a>
        </div>
        <div class="x-fax-number"></div>
        <div class="mt-4">
            <button class="btn btn-secondary x-send">送信</button>
            <a href="javascript:void(0)" class="x-cancel ml-2">キャンセル</a>
        </div>
    </div>
`;

export class SendFax {
    constructor(rest, pdfFile, faxNumber){
        this.rest = rest;
        this.pdfFile = pdfFile;
        this.faxNumber = faxNumber;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.pdfFile.innerText = getFilePart(pdfFile);
        this.map.view.addEventListener("click", event => this.doPreview());
        this.map.send.addEventListener("click", async event => await this.doSend());
        this.map.cancel.addEventListener("click", event => this.ele.remove());
    }

    doPreview(){
        let url = this.rest.url("/show-pdf", {file: this.pdfFile});
        window.open(url, "_blank");
    }

    async doSend(){
        let faxSid = await this.rest.sendFax(this.faxNumber, this.pdfFile);
        this.ele.dispatchEvent(new CustomEvent("sent", {detail: faxSid}));
    }

}

class SendFaxOrig extends Component {
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