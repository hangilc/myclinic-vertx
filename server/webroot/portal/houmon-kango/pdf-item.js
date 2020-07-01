import {Component, parseElement} from "./component.js";
import * as kanjidate from "../js/kanjidate.js";

let template = `
    <div>
        <span class="x-label"></span>
        <a href="javascript:void(0)" class="x-preview ml-2">プレビュー</a>
        <a href="javascript:void(0)" class="x-save ml-2">保存</a>
        <a href="javascript:void(0)" class="x-delete-tmp ml-2">一時ファイル削除</a>
    </div>
`;

export class PdfItem extends Component {
    constructor(url, patientId, rest) {
        super($(template));
        this.url = url;
        this.patientId = patientId;
        this.rest = rest;
        let map = parseElement(this.ele);
        this.labelElement = map.label;
        this.previewElement = map.preview;
        this.saveElement = map.save;
        this.deleteTmpElement = map.deleteTmp;
        this.init();
    }

    init(){
        this.labelElement.text(this.createLabel());
        this.previewElement.on("click", event => this.doPreview());
        if( this.patientId > 0 ) {
            this.saveElement.on("click", event => this.doSave());
        } else {
            this.saveElement.addClass("d-none");
        }
        this.deleteTmpElement.on("click", event => this.doDeleteTmp());
    }

    onDeleted(cb){
        this.on("deleted", () => cb());
    }

    async doDeleteTmp(){
        let url = this.url;
        if( url ){
            await this.rest.deleteAppFile(url);
            this.trigger("deleted");
        }
    }

    async doSave(){
        let patientId = this.patientId;
        if( patientId > 0 ){
            let src = this.url;
            let stamp = kanjidate.getTimestamp();
            let dst = `/paper-scan/${patientId}/${patientId}-houmon-kango-${stamp}.pdf`;
            let result = await this.rest.moveAppFile(src, dst);
            if( result === true ){
                alert("ファイルが保存されました。");
                this.deleteTmpElement.addClass("d-none");
            }
        }
    }

    createLabel(){
        let url = this.url;
        if( url.length > 10 ){
            return url.substring(0, 10) + "...";
        } else {
            return url;
        }
    }

    doPreview(){
        let url = this.url;
        window.open(url, "_blank");
    }
}