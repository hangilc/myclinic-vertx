import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import * as paperscan from "../../js/paper-scan.js";
import {ScannedItem} from "./scanned-item.js";
import * as STATUS from "./status.js";

let tmpl = `
<div class="scan-widget border border-dark rounded p-3 mb-3">
    <div class="mb-2">
        <div class="h4">患者選択</div>
        <form class="form-inline mb-2 x-search-patient-form" onsubmit="return false;">
            <input type="text" class="form-control mr-2 x-search-patient-text"/> 
            <button class="btn btn-primary x-search-patient-button" type="submit">検索</button>
        </form>
        <div class="x-patient-search-result-wrapper d-none">
            <select size="10" class="x-patient-search-result form-control mb-2 col-6"></select>
            <button class="btn btn-primary btn-sm mr-2 x-select-patient-button">選択</button>
            <button class="btn btn-link btn-sm x-search-patient-close">閉じる</button>
        </div>
    </div>
    <div class="x-selected-patient-disp-wrapper mb-2 border border-success rounded p-2">
        選択された患者：<span class="x-selected-patient-disp"></span>
    </div>
    <div class="mb-2">
        <div class="h4">文書の種類</div>
        <select class="form-control w-auto x-tag-select">
            <option value="hokensho">保険証</option>
            <option value="health-check">健診結果</option>
            <option value="exam-report">検査結果</option>
            <option value="refer">紹介状</option>
            <option value="shijisho">訪問看護指示書など</option>
            <option value="zaitaku">訪問看護などの報告書</option>
            <option value="image" selected>その他</option>
        </select>
    </div>
    <div class="mb-2"> 
        <div class="h4">スキャナ選択</div>
         <div class="form-inline mb-2"> 
            <select class="form-control x-device-list mr-2"></select>
            <button class="btn btn-secondary x-refresh-device-list">更新</button>
        </div>
   </div>
    <div class="mb-2">
        <div class="mb-2">
            <button class="btn btn-primary mr-2 x-start-scan">スキャン開始</button>
            <span class="x-scan-progress"></span>
        </div>
        <div class="x-scanned-image-list mb-2 border border-success rounded p-2"> 
            <div class="x-scanned-items mb-2"></div>
            <div class="x-upload-commands"> 
                <button class="btn btn-primary x-upload-button">アップロード</button>
            </div>
            <div class="d-none x-re-upload-commands">
                <span class="text-danger mr-2">アップロードに失敗しました。</span>
                <button class="btn btn-secondary x-retry-upload">再試行</button>
                <button class="btn btn-secondary x-cancel-upload">キャンセル</button>
            </div>
        </div>
    </div>
    <div class="d-flex align-items-center"> 
        <div class="h3 d-inline-block mr-auto"></div>
        <button class="btn btn-secondary btn-sm x-cancel-widget">キャンセル</button>
    </div>
</div>
`;

export class ScanWidget {
    constructor(rest, printAPI) {
        this.rest = rest;
        this.printAPI = printAPI;
        this.patient = null;
        this.items = [];
        this.jobName = null;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.status = new Status(this);
        this.map.searchPatientForm.addEventListener("submit", async event =>
            await this.doSearchPatient());
        this.map.searchPatientClose.addEventListener("click", event =>
            this.doSearchPatientClose());
        this.map.selectPatientButton.addEventListener("click", async event => {
            let patient = await this.doSelectPatient();
            if (patient) {
                this.setPatient(patient);
                this.enableUpload(true);
                this.firePatientChanged();
            }
        });
        this.map.refreshDeviceList.addEventListener("click", async event => {
            await this.loadDeviceList();
            this.enableScan(this.map.deviceList.value);
        });
        this.map.startScan.addEventListener("click", async event => {
            this.enablePatientSelection(false);
            this.enableTagSelection(false);
            this.enableScannerSelection(false);
            this.enableScan(false);
            this.enableUpload(false);
            this.enableCancel(false);
            this.fireScanStarted();
            try {
                let item = await this.doStartScan();
                this.items.push(item);
                this.renameItems();
                this.map.scannedItems.append(item.ele);
                item.ele.addEventListener("rescan", async event => {

                });
                if (this.jobName == null) {
                    let uploadJob = this.createUploadJob();
                    this.jobName = await this.printAPI.createUploadJob(uploadJob);
                }
            } finally {
                this.enablePatientSelection(true);
                this.enableTagSelection(true);
                this.enableScannerSelection(true);
                this.enableScan(true);
                this.enableUpload(this.items.length > 0);
                this.enableCancel(true);
                this.fireScanEnded();
            }
        });
        this.map.uploadButton.addEventListener("click", async event => {
            this.enablePatientSelection(false);
            this.enableTagSelection(false);
            this.enableScannerSelection(false);
            this.enableScan(false);
            this.enableUpload(false);
            this.enableCancel(false);
            try {
                for (let item of this.items) {
                    await item.upload();
                }
                await this.deleteScannedFiles();
                await this.printAPI.deleteUploadJob(this.jobName);
                this.fireRemove();
                alert("アップロードが終了しました。");
            } catch (e) {
                this.enablePatientSelection(true);
                this.enableTagSelection(true);
                this.enableScannerSelection(true);
                this.enableScan(true);
                this.enableUpload(this.items.length > 0);
                this.enableCancel(true);
            }
        });
        this.enableScan(false);
        this.enableUpload(false);
        this.enableCancel(false);

        // this.ele.addEventListener("rescan-item", async event => {
        //     let item = event.detail;
        //     this.changeStatusTo(STATUS.SCANNING);
        //     try {
        //         await this.doReScan(item);
        //     } finally {
        //         this.renameItems();
        //         this.changeStatusTo(STATUS.PREPARING);
        //     }
        // });
        // this.map.retryUpload.addEventListener("click", async event => {
        //     try {
        //         await this.doRetryUpload();
        //         this.changeStatusTo(STATUS.UPLOADED);
        //         await this.deleteScannedFiles();
        //         await this.printAPI.deleteUploadJob(this.jobName);
        //         alert("アップロードが終了しました。");
        //         this.closeWidget();
        //     } catch (e) {
        //         console.log(e.toString());
        //     }
        // });
        // this.map.cancelUpload.addEventListener("click", async event => {
        //     await this.doCancelUpload();
        //     this.changeStatusTo(STATUS.PREPARING);
        // });
        // this.ele.addEventListener("delete-item", async event => {
        //     let item = event.detail;
        //     await item.deleteScannedFile();
        //     item.ele.remove();
        //     let index = this.items.indexOf(item);
        //     this.items.splice(index, 1);
        //     this.renameItems();
        //     this.status.updateUI();
        // });
        // this.map.tagSelect.addEventListener("change", event => {
        //     if (this.status.isPreparing()) {
        //         this.renameItems();
        //     }
        // });
        // this.map.cancelWidget.addEventListener("click", async event => {
        //     if (this.items.length > 0) {
        //         if (!confirm("このスキャンを本当にキャンセルしますか？")) {
        //             return;
        //         }
        //     }
        //     await this.deleteScannedFiles();
        //     if( this.jobName ){
        //         this.printAPI.deleteUploadJob(this.jobName);
        //     }
        //     this.closeWidget()
        // });
        // this.ele.addEventListener("suppress-scan", event => {
        //     this.status.suppressScan = true;
        //     this.status.updateUI();
        // });
        // this.ele.addEventListener("release-scan", event => {
        //     this.status.suppressScan = false;
        //     this.status.updateUI();
        // });
        // if( this.patient ){
        //     this.setPatient(this.patient);
        // }
        // this.items.forEach(item => this.map.scannedItems.append(item.ele));
        // this.renameItems();
    }

    firePatientChanged() {
        this.ele.dispatchEvent(new CustomEvent("patient-changed", {
            detail: this.patient
        }));
    }

    fireScanStarted() {
        this.ele.dispatchEvent(new Event("scan-started"));
    }

    fireScanEnded(){
        this.ele.dispatchEvent(new Event("scan-ended"));
    }

    fireRemove(){
        this.ele.dispatchEvent(new Event("remove"));
    }

    async postConstruct() {
        await this.loadDeviceList();
        this.enableScan(this.map.deviceList.value);
    }

    async deleteScannedFiles() {
        for (let item of this.items) {
            await item.deleteScannedFile();
        }
    }

    updateUI() {
        this.status.updateUI();
    }

    closeWidget() {
        this.ele.dispatchEvent(new Event("remove"));
    }

    changeStatusTo(status) {
        this.status.changeStatusTo(status);
        this.status.updateUI();
    }

    async loadDeviceList() {
        let devices = await this.printAPI.listScannerDevices();
        let select = this.map.deviceList;
        select.innerHTML = "";
        for (let device of devices) {
            let opt = document.createElement("option");
            opt.innerText = device.name;
            opt.value = device.deviceId;
            select.appendChild(opt);
        }
    }

    focus() {
        this.map.searchPatientText.focus();
    }

    async doSearchPatient() {
        let text = this.map.searchPatientText.value.trim();
        if (text === "") {
            return;
        }
        let result = await this.rest.searchPatient(text);
        let select = this.map.patientSearchResult;
        select.innerHTML = "";
        if (result.length === 1) {
            select.size = 2;
        } else if (result.length > 10) {
            select.size = 10;
        } else {
            select.size = result.length;
        }
        select.scrollTop = 0;
        for (let patient of result) {
            let opt = document.createElement("option");
            opt.value = patient.patientId;
            opt.innerText = patientRep(patient);
            select.appendChild(opt);
        }
        if (result.length > 0) {
            select.querySelector("option").selected = true;
        }
        this.map.patientSearchResultWrapper.classList.remove("d-none");
        select.focus();
    }

    doSearchPatientClose() {
        this.map.patientSearchResult.innerHTML = "";
        this.map.patientSearchResultWrapper.classList.add("d-none");
    }

    async doSelectPatient() {
        let patientId = this.map.patientSearchResult.value;
        if (!patientId) {
            return null;
        }
        let patient = this.patient = await this.rest.getPatient(patientId);
        if (patient) {
            this.map.patientSearchResultWrapper.classList.add("d-none");
            this.map.searchPatientText.value = "";
            return patient;
        } else {
            return null;
        }
    }

    setPatient(patient) {
        this.patient = patient;
        this.map.selectedPatientDisp.innerText = patientRep(patient);
    }

    async doStartScan() {
        let deviceId = this.map.deviceList.value;
        if (!deviceId) {
            throw new Error("患者が設定されていません。");
        }
        this.map.scanProgress.innerText = "スキャンの準備中";
        let file = await this.printAPI.scan(deviceId, pct => {
            this.map.scanProgress.innerText = `${pct}%`;
        });
        this.map.scanProgress.innerText = "";
        return new ScannedItem(file, "", this.printAPI, this.rest);
    }

    async doReScan(item) {
        let deviceId = this.map.deviceList.value;
        if (!deviceId) {
            throw new Error("患者が設定されていません。");
        }
        this.map.scanProgress.innerText = "スキャンの準備中";
        let resolution = 100;
        let file = await this.printAPI.scan(deviceId, pct => {
            this.map.scanProgress.innerText = `${pct}%`;
        }, resolution);
        this.map.scanProgress.innerText = "スキャン終了";
        await item.deleteScannedFile();
        item.setScannedFile(file);
        await item.doDisp();
    }

    getPatientId() {
        if (this.patient) {
            return this.patient.patientId;
        } else {
            return null;
        }
    }

    getTag() {
        return this.map.tagSelect.value;
    }

    renameItems() {
        let patientId = this.getPatientId();
        let patientIdTag = patientId ? ("" + patientId) : "????";
        let tag = this.getTag();
        let timestamp = paperscan.getTimestamp();
        let items = this.items;
        if (items.length === 1) {
            let item = items[0];
            let ext = paperscan.getFileExtension(item.getScannedFile());
            item.setUpload(paperscan.createPaperScanFileName(patientIdTag, tag, timestamp, "", ext),
                patientId);
        } else {
            let ser = 1;
            for (let item of items) {
                let ext = paperscan.getFileExtension(item.getScannedFile());
                item.setUpload(paperscan.createPaperScanFileName(patientIdTag, tag, timestamp, "" + ser, ext),
                    patientId);
                ser += 1;
            }
        }
    }

    createUploadJob() {
        let files = this.items.map(item => ({
            scannedFileName: item.scannedFile,
            uploadFileName: item.uploadName
        }));
        return {
            "patientId": this.getPatientId(),
            "uploadFiles": files
        }
    }

    // async doUpload() {
    //     for (let item of this.items) {
    //         await item.upload();
    //     }
    // }

    async doRetryUpload() {
        for (let item of this.items) {
            if (!item.isUploaded()) {
                await item.upload();
            }
        }
    }

    async doCancelUpload() {
        for (let item of this.items) {
            if (item.isUploaded()) {
                await item.deleteUploadedImage();
            }
            let newItem = new ScannedItem(item.savedName, item.printAPI, item.rest);
            newItem.setUpload(item.uploadName, item.patientId);
            item.ele.parentNode.replaceChild(newItem.ele, item.ele);
            for (let i = 0; i < this.items.length; i++) {
                if (this.items[i] === item) {
                    this.items[i] = newItem;
                    break;
                }
            }
        }
    }

    enablePatientSelection(enabled) {
        let disabled = !enabled;
        this.map.searchPatientText.disabled = disabled;
        this.map.searchPatientButton.disabled = disabled;
    }

    enableScannerSelection(enabled) {
        let disabled = !enabled;
        this.map.deviceList.disabled = disabled;
        this.map.refreshDeviceList.disabled = disabled;
    }

    enableTagSelection(enabled) {
        this.map.tagSelect.disabled = !enabled;
    }

    enableScan(enabled) {
        this.map.startScan.disabled = !enabled;
        for (let item of this.items) {
            item.enableScan(enabled);
        }
    }

    enableUpload(enabled) {
        let disabled = !enabled;
        if( !(this.patient && this.map.deviceList.value) ){
            disabled = true;
        }
        this.map.uploadButton.disabled = disabled;
        this.map.retryUpload.disabled = disabled;
        this.map.cancelUpload.disabled = disabled;
    }

    enableCancel(enabled) {
        this.map.cancelWidget.disabled = !enabled;
    }
}

function patientRep(patient) {
    let patientIdRep = ("" + patient.patientId).padStart(4, "0");
    return `(${patientIdRep}) ${patient.lastName}${patient.firstName}`;
}

class Status {
    constructor(panel) {
        this.panel = panel;
        this.map = panel.map;
        this.items = panel.items;
        this.status = STATUS.PREPARING;
        this.suppressScan = false;
    }

    changeStatusTo(status) {
        if (STATUS.changeMap[this.status].includes(status)) {
            this.status = status;
        } else {
            throw new Error(`Invalid state transition from ${this.status} to ${status}`);
        }
    }

    isPreparing() {
        return this.status === STATUS.PREPARING;
    }

    isScanning() {
        return this.status === STATUS.SCANNING;
    }

    isUploading() {
        return this.status === STATUS.UPLOADING;
    }

    isUploaded() {
        return this.status === STATUS.UPLOADED;
    }

    isPartiallyUploaded() {
        return this.status === STATUS.PARTIALLY_UPLOADED;
    }

    updateUI() {
        // let map = this.map;
        // let status = this.status;
        // enableUI(
        //     [map.searchPatientText, map.searchPatientButton],
        //     [STATUS.PREPARING, STATUS.SCANNING].includes(status));
        // enableUI(
        //     [map.selectPatientButton, map.tagSelect, map.deviceList, map.refreshDeviceList],
        //     [STATUS.PREPARING].includes(status)
        // );
        // enableUI(
        //     [map.startScan],
        //     [STATUS.PREPARING].includes(status) && !!map.deviceList.value && !this.suppressScan
        // );
        // enableUI(
        //     [map.uploadButton],
        //     [STATUS.PREPARING].includes(status) && this.items.length > 0
        // );
        // showUI(
        //     [map.reUploadCommands],
        //     [STATUS.PARTIALLY_UPLOADED].includes(status)
        // );
        // showUI(
        //     [map.scanProgress],
        //     [STATUS.SCANNING].includes(status)
        // );
        // for (let item of this.items) {
        //     item.updateUI(this.status);
        // }
    }
}
