import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import * as paperscan from "../../js/paper-scan.js";

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
        <div class="h4">文書の種類</div>
        <select class="form-control w-auto">
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
                <button class="btn btn-primary x-upload-button">アップロード</button>
            </div>
        </div>
    </div>
</div>
`;

let scannedImageTmpl = `
    <div>
        <span class="x-name mr-2"></span>
        <button class="btn btn-link x-disp">表示</button>
        <div class="x-preview"></div>
    </div>
`;

let previewTmpl = `
<div class="d-flex align-items-start">
    <div class="x-image-wrapper d-inline-block p-2 border border-info rounded mr-2"></div>
    <button class="btn btn-secondary btn-sm x-close">閉じる</button>
</div>
`;

class PreviewBox {
    constructor(blob){
        this.ele = createElementFrom(previewTmpl);
        let map = parseElement(this.ele);
        let img = document.createElement("img");
        img.src = URL.createObjectURL(new Blob([blob.buffer], {type: "image/jpg"}));
        let scale = 1.8;
        img.width = 210 * scale;
        img.height = 297 * scale;
        map.imageWrapper.append(img);
        map.close.addEventListener("click", event => this.ele.remove());
    }
}

class ScannedItem {
    constructor(name, printAPI){
        this.name = name;
        this.printAPI = printAPI;
        this.ele = createElementFrom(scannedImageTmpl);
        this.map = parseElement(this.ele);
        this.map.name.innerText = name;
        this.map.disp.addEventListener("click", async event => await this.doDisp());
    }

    async getImageData(){
        return await this.printAPI.getScannedImage(this.name);
    }

    async doDisp(){
        let blob = await this.getImageData();
        let pbox = new PreviewBox(blob);
        this.map.preview.append(pbox.ele);
    }
}

export class ScanPanel {
    constructor(rest, printAPI){
        this.rest = rest;
        this.printAPI = printAPI;
        this.items = [];
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.searchPatientButton.addEventListener("click", async event => await this.doSearchPatient());
        this.map.selectPatientButton.addEventListener("click", async event => await this.doSelectPatient());
        this.map.refreshDeviceList.addEventListener("click", async event => await this.reloadHook());
        this.map.startScan.addEventListener("click", async event => await this.doStartScan());
        this.map.uploadButton.addEventListener("click", async event => await this.doUpload());
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
        let patient = this.patient = await this.rest.getPatient(patientId);
        this.map.selectedPatientDisp.innerText = patientRep(patient);
        this.map.patientSearchResultWrapper.classList.add("d-none");
    }

    async doStartScan(){
        let deviceId = this.map.deviceList.value;
        if( !deviceId ){
            console.log("scan device not selected");
            return;
        }
        let file = await this.printAPI.scan(deviceId, pct => console.log(pct));
        let item = new ScannedItem(file, this.printAPI);
        this.items.push(item);
        this.map.scannedItems.append(item.ele);
    }

    getPatientId(){
        return parseInt(this.map.patient)
    }

    async doUpload(){
        let file = new File(["hello"], "test.txt");
        await this.rest.uploadFileBlob("/save-patient-image",
            ["hello, world"],
            "hello.txt",
            {"patient-id": 3});
        // let items = this.items;
        // if( items.length === 0 ){
        //     return;
        // } else if( items.length === 1 ){
        //     let item = items[0];
        // }
    }

}

function patientRep(patient){
    let patientIdRep = ("" + patient.patientId).padStart(4, "0");
    return `(${patientIdRep}) ${patient.lastName}${patient.firstName}`;
}