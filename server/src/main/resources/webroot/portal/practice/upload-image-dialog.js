import {Dialog} from "./dialog2.js";
import {Component} from "./component2.js";
import {parseElement} from "../js/parse-element.js";
import {getTimestamp} from "../js/kanjidate.js";

let bodyTemplate = `
    <div>
        <div class="form-inline">
            Tag: <input type="text" class="x-tag ml-2" value="image"/> 
            <div class="dropdown ml-2">
              <button class="btn btn-link dropdown-toggle" 
                type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                例
              </button>
              <div class="dropdown-menu x-tag-examples">
                <a class="dropdown-item x-image" href="javascript:void(0)" data-tag="image">画像</a>
                <a class="dropdown-item x-hokensho" href="javascript:void(0)" data-tag="hokensho">保険証</a>
                <a class="dropdown-item x-checkup" href="javascript:void(0)" data-tag="checkup">健診結果</a>
                <a class="dropdown-item x-checkup" href="javascript:void(0)" data-tag="zaitaku">在宅報告</a>
                <a class="dropdown-item x-checkup" href="javascript:void(0)" data-tag="douisho">同意書</a>
              </div>
            </div>
        </div>
        <input type="file" multiple class="x-file mt-3"/>
    </div>
`;

class Body extends Component {
    constructor(){
        super($(bodyTemplate));
        let map = parseElement(this.ele);
        this.fileElement = map.file;
        this.tagElement = map.tag;
        map.tagExamples.find("a").on("click", event => {
            let tag = $(event.target).data("tag");
            if( tag ){
                map.tag.val(tag);
            }
        });
    }

    getFiles(){
        return this.fileElement.get(0).files;
    }

    getTag(){
        return this.tagElement.val();
    }
}

let footerTemplate = `
    <div class="d-flex justify-content-end">
        <button type="button" class="btn btn-primary x-save">保存</button>
        <button type="button" class="btn btn-secondary x-cancel ml-2">キャンセル</button>
    </div>
`;

class Footer extends Component {
    constructor(){
        super($(footerTemplate));
        let map = parseElement(this.ele);
        map.save.on("click", event => this.trigger("save"));
        map.cancel.on("click", event => this.trigger("cancel"));
    }

    onSave(cb){
        this.on("save", cb);
    }

    onCancel(cb){
        this.on("cancel", cb);
    }
}

export class UploadImageDialog extends Dialog {
    constructor(patientId){
        super();
        this.patientId = patientId;
        this.setDialogTitle("画像保存");
        let body = this.body = new Body();
        this.appendToBody(body.ele);
        let footer = new Footer();
        this.appendToFooter(footer.ele);
        footer.onSave(() => this.doSave());
        footer.onCancel(() => this.close());
    }

    async doSave(){
        let patientId = this.patientId;
        if( patientId > 0 ){
            let formData = new FormData();
            formData.append("patient-id", "" + patientId);
            let files = this.body.getFiles();
            if( files.length > 0 ){
                let stamp = getTimestamp();
                let index = 1;
                for(let file of files){
                    let ext = getFileExtension(file.name);
                    let tag = this.body.getTag();
                    let ser = "";
                    if( files.length > 1 ){
                        ser = `(${index})`;
                        index += 1;
                    }
                    let filename = `${patientId}-${tag}-${stamp}${ser}${ext}`;
                    formData.append(`file${index}`, file, filename);
                }
                $.ajax("/json/save-patient-image", {
                    data: formData,
                    processData: false,
                    contentType: false,
                    method: "POST",
                    success: () => this.close(),
                    error: (xhr, status, err) => {
                        let msg = xhr.responseText + " : " + err.toString() + " : " + status;
                        alert(msg);
                    }
                });
            }
        }
    }
}

function getFileExtension(filename){
    let i = filename.lastIndexOf(".");
    if( i < 0 ){
        return "";
    } else {
        return filename.substring(i);
    }
}