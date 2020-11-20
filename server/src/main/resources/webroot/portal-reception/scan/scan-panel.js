import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import * as paperscan from "../../js/paper-scan.js";
import {ScannedItem} from "./scanned-item.js";

let tmpl = `
<div>
    <div class="h3">スキャン</div>
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
            <button class="btn btn-primary x-start-scan">スキャン開始</button>
        </div>
        <div class="x-scanned-image-list mb-2 border border-success rounded p-2"> 
            <div class="x-scanned-items mb-2"></div>
            <div> 
                <button class="btn btn-primary x-upload-button" disabled>アップロード</button>
            </div>
        </div>
    </div>
</div>
`;


export class ScanPanel {
    constructor(rest, printAPI) {
        this.rest = rest;
        this.printAPI = printAPI;
        this.status = "preparing";
        this.items = [];
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.searchPatientForm.addEventListener("submit", async event => await this.doSearchPatient());
        this.map.searchPatientClose.addEventListener("click", event => this.doSearchPatientClose());
        this.map.selectPatientButton.addEventListener("click", async event => await this.doSelectPatient());
        this.map.refreshDeviceList.addEventListener("click", async event => await this.reloadHook());
        this.map.startScan.addEventListener("click", async event => await this.doStartScan());
        this.map.uploadButton.addEventListener("click", async event => await this.doUpload());
        this.ele.addEventListener("patient-changed", event => {
            let patient = event.detail;
            this.map.selectedPatientDisp.innerText = patientRep(patient);
            this.renameItems();
        });
        this.map.tagSelect.addEventListener("change", event => {
            this.renameItems();
        });
        this.ele.addEventListener("scan-started", event => {
            this.map.startScan.disabled = true;
        });
        this.ele.addEventListener("scan-ended", event => {
            this.map.startScan.disabled = false;
        });
        this.ele.addEventListener("items-changed", event => {
            this.updateUploadButtonEnable();
        });
    }

    changeStatus(status){
        switch(status){
            case "initial": {

            }
        }
    }

    async reloadHook() {
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
        console.log("ScanPanel.focus");
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
        if( result.length === 1 ){
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
            return;
        }
        let patient = this.patient = await this.rest.getPatient(patientId);
        if( patient ) {
            this.map.patientSearchResultWrapper.classList.add("d-none");
            this.map.searchPatientText.value = "";
            this.ele.dispatchEvent(new CustomEvent("patient-changed", {detail: patient}));
        }
    }

    async doStartScan() {
        this.ele.dispatchEvent(new Event("scan-started"));
        try {
            let deviceId = this.map.deviceList.value;
            if (!deviceId) {
                console.log("scan device not selected");
                return;
            }
            let file = await this.printAPI.scan(deviceId, pct => console.log(pct));
            let item = new ScannedItem(file, this.printAPI, this.rest);
            this.items.push(item);
            this.renameItems();
            this.map.scannedItems.append(item.ele);
        } finally {
            this.ele.dispatchEvent(new Event("scan-ended"));
        }
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
            let ext = paperscan.getFileExtension(item.getSavedName());
            item.setUpload(paperscan.createPaperScanFileName(patientIdTag, tag, timestamp, "", ext),
                patientId);
        } else {
            let ser = 1;
            for (let item of items) {
                let ext = paperscan.getFileExtension(item.getSavedName());
                item.setUpload(paperscan.createPaperScanFileName(patientIdTag, tag, timestamp, "" + ser, ext),
                    patientId);
                ser += 1;
            }
        }
    }

    async doUpload() {
        try {
            for (let item of this.items) {
                await item.upload();
            }
        } catch(e){
            alert(e.toString());
        }
    }

    updateUploadButtonEnable(){

    }

}

function patientRep(patient) {
    let patientIdRep = ("" + patient.patientId).padStart(4, "0");
    return `(${patientIdRep}) ${patient.lastName}${patient.firstName}`;
}