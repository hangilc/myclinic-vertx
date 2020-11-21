import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {PreviewBox} from "./preview-box.js";

let tmpl = `
    <div>
        <div> 
            <svg class="bi bi-check text-success d-none x-success-icon" width="2em" height="2em" viewBox="0 0 16 16" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                <path fill-rule="evenodd" d="M10.97 4.97a.75.75 0 0 1 1.071 1.05l-3.992 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.236.236 0 0 1 .02-.022z"/>
            </svg>
            <svg class="bi bi-x text-danger d-none x-failure-icon" width="2em" height="2em" viewBox="0 0 16 16" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
              <path fill-rule="evenodd" d="M11.854 4.146a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708-.708l7-7a.5.5 0 0 1 .708 0z"/>
              <path fill-rule="evenodd" d="M4.146 4.146a.5.5 0 0 0 0 .708l7 7a.5.5 0 0 0 .708-.708l-7-7a.5.5 0 0 0-.708 0z"/>
            </svg>
           <span class="x-name mr-2"></span>
            <button class="btn btn-link x-disp">表示</button>
        </div>
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

    isBeforeUpload(){
        return this.state === "before-upload";
    }

    isUploaded(){
        return this.state === "uploaded";
    }

    isUploadFailed(){
        return this.state === "upload-failed";
    }

    async getImageData(){
        return await this.printAPI.getScannedImage(this.savedName);
    }

    setUpload(uploadName, patientId){
        if( this.isBeforeUpload() ) {
            this.uploadName = uploadName;
            this.patientId = patientId;
            this.map.name.innerText = uploadName;
        }
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
        this.state = "uploading";
        try {
            await this.rest.uploadFileBlob("/save-patient-image",
                [buf],
                this.uploadName,
                {"patient-id": this.patientId});
            this.state = "uploaded";
            this.map.failureIcon.classList.add("d-none");
            this.map.successIcon.classList.remove("d-none");
        } catch(e){
            this.state = "upload-failed";
            this.map.failureIcon.classList.remove("d-none");
            throw e;
        }
    }

}
