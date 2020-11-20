import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {PreviewBox} from "./preview-box.js";

let tmpl = `
    <div>
        <span class="x-name mr-2"></span>
        <button class="btn btn-link x-disp">表示</button>
        <div class="x-preview"></div>
    </div>
`;

export class ScannedItem {
    constructor(savedName, printAPI, rest){
        this.savedName = savedName;
        this.printAPI = printAPI;
        this.rest = rest;
        this.state = "before-upload";
        this.uploadName = null;
        this.patientId = null;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.name.innerText = "";
        this.map.disp.addEventListener("click", async event => await this.doDisp());
    }

    async getImageData(){
        return await this.printAPI.getScannedImage(this.savedName);
    }

    setUpload(uploadName, patientId){
        this.uploadName = uploadName;
        this.patientId = patientId;
        this.map.name.innerText = uploadName;
    }

    getSavedName(){
        return this.savedName;
    }

    async doDisp(){
        let buf = await this.getImageData();
        let pbox = new PreviewBox(buf);
        this.map.preview.append(pbox.ele);
    }

    async upload(){
        if( !this.patientId ){
            throw new Error("患者が設定されていません。");
        }
        if( !this.uploadName ){
            throw new Error("アップロドー・ファイル名が設定されていません。");
        }
        let buf = await this.getImageData();
        await this.rest.uploadFileBlob("/save-patient-image",
            [buf],
            this.uploadName,
            {"patient-id": this.patientId});

    }
}
