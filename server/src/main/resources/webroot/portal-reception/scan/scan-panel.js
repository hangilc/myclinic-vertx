import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";

let tmpl = `
<div>
    <div class="h3">スキャン</div>
    <div class="mb-2">
        <div class="h4">患者選択</div>
        <div class="form-inline mb-2">
            <input type="text" class="form-control mr-2 x-search-patient-text" /> 
            <button class="btn btn-primary x-search-patient-button">検索</button>
        </div>
        <div class="x-patient-search-result-wrapper d-none">
            <select size="10" class="x-patient-search-result form-control mb-2 col-6"></select>
            <button class="btn btn-primary x-select-patient-button">選択</button>
        </div>
    </div>
    <div class="x-selected-patient-disp-wrapper mb-2 border border-success rounded p-2">
        選択された患者：<span class="x-selected-patient-disp"></span>
    </div>
    <div class="mb-2">
        <div class="form-inline mb-2"> 
            <select class="form-control x-device-list mr-2"></select>
            <button class="btn btn-secondary x-refresh-device-list">更新</button>
        </div>
        <div class="mb-2">
            <button class="btn btn-primary x-start-scan">スキャン開始</button>
        </div>
        <div class="x-scanned-image-list mb-2 border border-success rounded p-2"> 
        </div>
    </div>
</div>
`;

let scannedImageTmpl = `
    <div>
        <span class="x-file-name"></span>
    </div>
`;

export class ScanPanel {
    constructor(rest, printAPI){
        this.rest = rest;
        this.printAPI = printAPI;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.searchPatientButton.addEventListener("click", async event => await this.doSearchPatient());
        this.map.selectPatientButton.addEventListener("click", async event => await this.doSelectPatient());
        this.map.refreshDeviceList.addEventListener("click", async event => await this.reloadHook());
        this.map.startScan.addEventListener("click", async event => await this.doStartScan());
    }

    async reloadHook(){
        let devices = await this.printAPI.listScannerDevices();
        let select = this.map.deviceList;
        select.innerHTML = "";
        for(let device of devices){
            let opt = document.createElement("option");
            opt.innerText = device.name;
            opt.value = device.deviceId;
            select.appendChild(opt);
        }
    }

    async doSearchPatient(){
        let text = this.map.searchPatientText.value.trim();
        if( text === "" ){
            return;
        }
        let result = await this.rest.searchPatient(text);
        let select = this.map.patientSearchResult;
        select.innerHTML = "";
        for(let patient of result){
            let opt = document.createElement("option");
            opt.value = patient.patientId;
            opt.innerText = patientRep(patient);
            select.appendChild(opt);
        }
        this.map.patientSearchResultWrapper.classList.remove("d-none");
    }

    async doSelectPatient(){
        let patientId = this.map.patientSearchResult.value;
        if( !patientId ){
            return;
        }
        let patient = await this.rest.getPatient(patientId);
        this.map.selectedPatientDisp.innerText = patientRep(patient);
        this.map.patientSearchResultWrapper.classList.add("d-none");
    }

    async doStartScan(){
        let deviceId = this.map.deviceList.value;
        if( !deviceId ){
            console.log("scan device not selected");
            return;
        }
        let file = await this.printAPI.startScan(deviceId);
        let item = this.createScannedImageItem(file);
        let wrapper = this.map.scannedImageList;
        wrapper.appendChild(item);
    }

    createScannedImageItem(fileName){
        let e = createElementFrom(scannedImageTmpl);
        let map = parseElement(e);
        map.fileName.innerText = fileName;
        return e;
    }
}

function patientRep(patient){
    let patientIdRep = ("" + patient.patientId).padStart(4, "0");
    return `(${patientIdRep}) ${patient.lastName}${patient.firstName}`;
}