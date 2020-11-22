import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {PreviewBox} from "./preview-box.js";
import * as STATUS from "./status.js";
import {showUI} from "../../js/dynamic-ui.js";

let tmpl = `
    <div>
        <div>
            <div class="x-success-icon-wrapper" style="display:none"> 
                <svg class="bi bi-check text-success x-success-icon" width="2em" height="2em" viewBox="0 0 16 16" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                    <path fill-rule="evenodd" d="M10.97 4.97a.75.75 0 0 1 1.071 1.05l-3.992 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.236.236 0 0 1 .02-.022z"/>
                </svg>
            </div>
            <div class="x-failure-icon-wrapper" style="display:none"> 
                <svg class="bi bi-x text-danger x-failure-icon" width="2em" height="2em" viewBox="0 0 16 16" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                  <path fill-rule="evenodd" d="M11.854 4.146a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708-.708l7-7a.5.5 0 0 1 .708 0z"/>
                  <path fill-rule="evenodd" d="M4.146 4.146a.5.5 0 0 0 0 .708l7 7a.5.5 0 0 0 .708-.708l-7-7a.5.5 0 0 0-.708 0z"/>
                </svg>
            </div>
           <span class="x-name mr-2"></span>
            <button class="btn btn-link x-disp">表示</button>
            <button class="btn btn-link x-re-scan">再スキャン</button>
            <button class="btn btn-link x-delete">削除</button>
        </div>
        <div class="x-preview"></div>
    </div>
`;

export class ScannedItem {
    constructor(scannedFile, printAPI, rest) {
        this.scannedFile = scannedFile;
        this.printAPI = printAPI;
        this.rest = rest;
        this.state = "before-upload";
        this.uploadName = null;
        this.patientId = null;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.name.innerText = "";
        this.map.disp.addEventListener("click", async event => await this.doDisp());
        this.map.reScan.addEventListener("click", event => {
            if( confirm("再スキャンしますか？") ){
                this.ele.dispatchEvent(new CustomEvent("rescan-item", {bubbles: true, detail: this}))
            }
        });
        this.map.delete.addEventListener("click", event => {
            if( confirm("このスキャンを削除していいですか？") ){
                this.ele.dispatchEvent(new CustomEvent("delete-item", {bubbles: true, detail: this}))
            }
        });
    }

    isBeforeUpload() {
        return this.state === "before-upload";
    }

    isUploaded() {
        return this.state === "uploaded";
    }

    isUploadFailed() {
        return this.state === "upload-failed";
    }

    async getImageData() {
        return await this.printAPI.getScannedImage(this.scannedFile);
    }

    setScannedFile(filename){
        this.scannedFile = filename;
    }

    setUpload(uploadName, patientId) {
        if (this.isBeforeUpload()) {
            this.uploadName = uploadName;
            this.patientId = patientId;
            this.map.name.innerText = uploadName;
        }
    }

    getScannedFile() {
        return this.scannedFile;
    }

    async doDisp() {
        let buf = await this.getImageData();
        let pbox = new PreviewBox(buf);
        this.map.preview.innerHTML = "";
        this.map.preview.append(pbox.ele);
    }

    async upload() {
        if (!this.patientId) {
            throw new Error("患者が設定されていません。");
        }
        if (!this.uploadName) {
            throw new Error("アップロドー・ファイル名が設定されていません。");
        }
        let buf = await this.getImageData();
        this.state = "uploading";
        try {
            await this.rest.savePatientImageBlob(this.patientId, [buf], this.uploadName);
            this.state = "uploaded";
            this.map.failureIconWrapper.style.display = "none";
            this.map.successIconWrapper.style.display = "inline-block";
        } catch (e) {
            this.state = "upload-failed";
            this.map.failureIconWrapper.style.display = "inline-block";
            throw e;
        }
    }

    async deleteScannedFile(){
        return await this.printAPI.deleteScannedFile(this.scannedFile);
    }

    async deleteUploadedImage() {
        return await this.rest.deletePatientImage(this.patientId, this.uploadName);
    }

    updateUI(status) {
        showUI(this.map.reScan, [STATUS.PREPARING].includes(status));
        showUI(this.map.delete, [STATUS.PREPARING].includes(status));
    }

}

