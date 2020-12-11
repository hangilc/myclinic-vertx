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
            <span class="d-none x-uploading-notice">アップローディング...</span>
        </div>
        <div class="x-preview"></div>
    </div>
`;

export class ScannedItem {
    constructor(scannedFile, uploadName, printAPI, rest) {
        this.scannedFile = scannedFile;
        this.uploadName = uploadName;
        this.printAPI = printAPI;
        this.rest = rest;
        this.isScanning = false;
        this.isUploading = false;
        this.state = "before-upload";
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.disp.addEventListener("click", async event => await this.doDisp());
        this.map.reScan.addEventListener("click", event => {
            if (confirm("再スキャンしますか？")) {
                this.fireReScan();
            }
        });
        this.map.delete.addEventListener("click", event => {
            if (confirm("このスキャンを削除していいですか？")) {
                htis.fireDelete();
            }
        });
        this.updateUploadNameUI();
    }

    fireReScan() {
        this.ele.dispatchEvent(new Event("rescan"));
    }

    fireDelete() {
        this.ele.dispatchEvent(new Event("delete"));
    }

    updateDisabled() {
        let isScanning = this.isScanning;
        let isUploading = this.isUploading;
        this.map.reScan.disabled = isScanning || isUploading;
        this.map.delete.disabled = isScanning || isUploading;
    }

    setUploadName(uploadName) {
        this.uploadName = uploadName;
    }

    updateUploadNameUI() {
        this.map.name.innerText = this.uploadName;
    }

    setStateUploading(){
        this.state = "uploading";
    }

    setStateUploaded() {
        this.state = "uploaded";
    }

    setStateUploadFailed() {
        this.state = "upload-failed";
    }

    isUploaded(){
        return this.state === "uploaded";
    }

    showSuccessIcon(show) {
        this.map.successIconWrapper.style.display = show ? "inline-block" : "none";
    }

    showFailureIcon(show) {
        this.map.failureIconWrapper.style.display = show ? "inline-block" : "none";
    }

    updateStateUI() {
        switch (this.state) {
            case "before-upload": {
                this.showSuccessIcon(false);
                this.showFailureIcon(false);
                this.map.uploadingNotice.classList.add("d-none");
                break;
            }
            case "uploading": {
                this.showSuccessIcon(false);
                this.showFailureIcon(false);
                this.map.uploadingNotice.classList.remove("d-none");
                break;
            }
            case "uploaded": {
                this.showSuccessIcon(true);
                this.showFailureIcon(false);
                this.map.uploadingNotice.classList.add("d-none");
                break;
            }
            case "upload-failed": {
                this.showSuccessIcon(false);
                this.showFailureIcon(true);
                this.map.uploadingNotice.classList.add("d-none");
                break;
            }
            default: {
                throw new Error(`Unknown scan-item state: ${this.state}`)
            }
        }
    }

    setScannedFile(filename) {
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

    async getImageData() {
        return await this.printAPI.getScannedImage(this.scannedFile);
    }

    async doDisp() {
        let buf = await this.getImageData();
        let pbox = new PreviewBox(buf);
        this.map.preview.innerHTML = "";
        this.map.preview.append(pbox.ele);
    }

    async upload(patientId) {
        if( !(patientId > 0) ){
            throw new Error("患者が指定されていません。");
        }
        if (!this.uploadName) {
            throw new Error("アップロード・ファイル名が設定されていません。");
        }
        let buf = await this.getImageData();
        this.setStateUploading();
        this.updateStateUI();
        try {
            await this.rest.savePatientImageBlob(patientId, [buf], this.uploadName);
            this.setStateUploaded();
            this.updateStateUI();
        } catch (e) {
            this.setStateUploadFailed();
            this.updateStateUI();
            throw e;
        }
    }

    async deleteScannedFile() {
        return await this.printAPI.deleteScannedFile(this.scannedFile);
    }

    // async deleteUploadedImage() {
    //     return await this.rest.deletePatientImage(this.patientId, this.uploadName);
    // }

}

