import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {PreviewBox} from "./preview-box.js";
import {click, show} from "../../js/dom-helper.js";

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

class ScannedItemDom {
    constructor(ele) {
        this.map = parseElement(ele);
    }

    getSuccessIconWrapper() {
        return this.map.successIconWrapper;
    }

    getFailureIconWrapper() {
        return this.map.failureIconWrapper;
    }

    getUploadFile() {
        return this.map.name;
    }

    getDisp() {
        return this.map.disp;
    }

    getReScan() {
        return this.map.reScan;
    }

    getDelete() {
        return this.map.delete;
    }

    getUploadingNotice() {
        return this.map.uploadingNotice;
    }

    getPreview() {
        return this.map.preview;
    }
}

export class ScannedItem {
    constructor(prop, scannedFile, uploadName) {
        this.prop = prop;
        this.scannedFile = scannedFile;
        this.uploadName = uploadName;
        this.ele = createElementFrom(tmpl);
        this.d = new ScannedItemDom(this.ele);
        this.state = "before-upload"; // "success", "failure"
        this.updateUploadFileUI();
        this.bindDisp();
        this.bindReScan();
        this.bindDelete();
    }

    fireReScan() {
        this.ele.dispatchEvent(new CustomEvent("re-scan", {bubbles: true, detail: this}));
    }

    fireDeleted() {
        this.ele.dispatchEvent(new CustomEvent("item-deleted", {bubbles: true, detail: this}));
    }

    bindDisp() {
        click(this.d.getDisp(), async event => await this.disp());
    }

    bindReScan() {
        click(this.d.getReScan(), event => {
            if (confirm("再スキャンを実施しますか？")) {
                this.fireReScan();
            }
        });
    }

    bindDelete() {
        click(this.d.getDelete(), async event => {
            if (confirm("このスキャン画像を削除しますか？")) {
                await this.deleteScannedFile();
                this.fireDeleted();
                this.ele.remove();
            }
        });
    }

    setScannedFile(file) {
        this.scannedFile = file;
    }

    getScannedFile() {
        return this.scannedFile;
    }

    updateUploadFileUI() {
        this.d.getUploadFile().innerText = this.uploadName;
    }

    setUploadFile(uploadName) {
        this.uploadName = uploadName;
        this.updateUploadFileUI();
    }

    isUploaded() {
        return this.state === "success";
    }

    showUploadingNotice(value) {
        show(this.d.getUploadingNotice(), value);
    }

    async disp() {
        let buf = await this.prop.printAPI.getScannedImage(this.scannedFile);
        let pbox = new PreviewBox(buf);
        let preview = this.d.getPreview();
        preview.innerHTML = "";
        preview.append(pbox.ele);
    }

    async upload() {
        let patient = this.prop.patient;
        if (!patient) {
            throw new Error("患者が指定されていません。");
        }
        let patientId = patient.patientId;
        if (!this.uploadName) {
            throw new Error("アップロード・ファイル名が設定されていません。");
        }
        let buf = await this.prop.printAPI.getScannedImage(this.scannedFile);
        try {
            this.showUploadingNotice(true);
            await this.prop.rest.savePatientImageBlob(patientId, [buf], this.uploadName);
            this.state = "success";
            this.updateUploadResultUI();
            return true;
        } catch (e) {
            console.log(e);
            this.state = "failure";
            this.updateUploadResultUI();
            return false;
        } finally {
            this.showUploadingNotice(false);
        }
    }

    async deleteScannedFile() {
        return await this.prop.printAPI.deleteScannedFile(this.scannedFile);
    }

    showSuccessIcon(show) {
        this.d.getSuccessIconWrapper().style.display = show ? "inline-block" : "none";
    }

    showFailureIcon(show) {
        this.d.getFailureIconWrapper().style.display = show ? "inline-block" : "none";
    }

    updateUploadResultUI() {
        switch (this.state) {
            case "before-upload": {
                this.showSuccessIcon(false);
                this.showFailureIcon(false);
                break;
            }
            case "success": {
                this.showSuccessIcon(true);
                this.showFailureIcon(false);
                break;
            }
            case "failure": {
                this.showSuccessIcon(false);
                this.showFailureIcon(true);
                break;
            }
        }
    }

    updateDisabled() {
        if (!this.prop.isBeforeUpload) {
            show(this.d.getReScan(), false);
            show(this.d.getDelete(), false);
        } else {

        }
    }
}

