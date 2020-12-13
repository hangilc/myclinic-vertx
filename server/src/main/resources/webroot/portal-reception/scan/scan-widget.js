import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
//import * as paperscan from "../../js/paper-scan.js";
import {ItemList} from "./item-list.js";
import {enable} from "../../js/dom-helper.js";
import {Notice} from "../components/notice.js";

let tmpl = `
<div class="scan-widget border border-info rounded p-3 mb-3">
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
            <button class="btn btn-primary mr-2 x-start-scan" disabled>スキャン開始</button>
            <span class="x-scan-progress"></span>
        </div>
        <div class="x-scanned-image-list mb-2 border border-success rounded p-2"> 
            <div class="x-scanned-items mb-2"></div>
            <div class="x-upload-commands"> 
                <button class="btn btn-primary x-upload-button" disabled>アップロード</button>
            </div>
        </div>
    </div>
    <div class="d-flex align-items-center"> 
        <div class="h3 d-inline-block mr-auto"></div>
        <button class="btn btn-secondary btn-sm x-cancel-widget">キャンセル</button>
    </div>
</div>
`;

function extendProp(prop, values) {
    return Object.assign(Object.create(prop), values);
}

function on(element, event, handler) {
    element.addEventListener(event, handler);
}

function click(element, handler) {
    on(element, "click", handler);
}

function submit(element, handler) {
    on(element, "submit", handler);
}

function show(element, show) {
    if (show) {
        element.classList.remove("d-none");
    } else {
        element.classList.add("d-none");
    }
}

class ScanWidgetDomHelper {
    constructor(ele) {
        this.map = parseElement(ele);
    }

    getSearchPatientText() {
        return this.map.searchPatientText;
    }

    getSearchPatientForm() {
        return this.map.searchPatientForm;
    }

    getSearchPatientResultWrapper() {
        return this.map.patientSearchResultWrapper;
    }

    getSearchPatientResult() {
        return this.map.patientSearchResult;
    }

    getSelectPatient() {
        return this.map.selectPatientButton;
    }

    getCloseSearchPatientResult() {
        return this.map.searchPatientClose;
    }

    getSelectedPatientDisp() {
        return this.map.selectedPatientDisp;
    }

    getTagSelect() {
        return this.map.tagSelect;
    }

    getDeviceList() {
        return this.map.deviceList;
    }

    getRefreshDeviceList() {
        return this.map.refreshDeviceList;
    }

    getStartScan() {
        return this.map.startScan;
    }

    getScanProgress() {
        return this.map.scanProgress;
    }

    getScannedItems() {
        return this.map.scannedItems;
    }

    getUpload() {
        return this.map.uploadButton;
    }

    getCancel() {
        return this.map.cancelWidget;
    }
}

export class ScanWidget {
    constructor(prop) {
        this.prop = extendProp(prop, {
            patient: null,
            getTag: () => this.getSelectedTag(),
            getSelectedScanner: () => this.getSelectedScanner(),
            isUploading: false,
            isBeforeUpload: true
        });
        this.ele = createElementFrom(tmpl);
        this.d = new ScanWidgetDomHelper(this.ele);
        this.itemList = new ItemList(this.prop);
        this.d.getScannedItems().append(this.itemList.ele);
        this.bindSearchPatient();
        this.bindSearchPatientSelect();
        this.bindSearchPatientClose();
        this.bindRefreshDeviceList();
        this.bindStartScan();
        this.bindReScan();
        this.bindUpload();
        this.bindCancel();
        this.bindItemDeleted();
    }

    async postConstruct() {
        await this.loadDeviceList();
        this.updateDisabled();
    }

    fireUseScanner(scanner) {
        this.ele.dispatchEvent(new CustomEvent("use-scanner", {bubbles: true, detail: scanner}));
    }

    fireUnuseScanner(scanner) {
        this.ele.dispatchEvent(new CustomEvent("unuse-scanner", {bubbles: true, detail: scanner}));
    }

    fireDeleted() {
        this.ele.dispatchEvent(new CustomEvent("widget-deleted", {
            bubbles: true,
            detail: this
        }));
    }

    bindSearchPatient() {
        submit(this.d.getSearchPatientForm(), async event => {
            let patients = await searchPatient(this.prop.rest, this.d.getSearchPatientText().value);
            if (patients.length > 0) {
                setSearchResult(this.d.getSearchPatientResult(), patients);
                show(this.d.getSearchPatientResultWrapper(), true);
                this.d.getSearchPatientResult().focus();
            }
        });
    }

    bindSearchPatientSelect() {
        click(this.d.getSelectPatient(), async event => {
            let patient = await getSelectedPatient(
                this.d.getSearchPatientResult(), this.prop.rest
            );
            if (patient) {
                this.prop.patient = patient;
                show(this.d.getSearchPatientResultWrapper(), false);
                this.setPatient(patient);
                this.itemList.renameUploadNames();
                this.updateDisabled();
            }
        })
    }

    bindSearchPatientClose() {
        click(this.d.getCloseSearchPatientResult(), event =>
            show(this.d.getSearchPatientResultWrapper(), false));
    }

    bindRefreshDeviceList() {
        click(this.d.getRefreshDeviceList(), async event => {
            await this.loadDeviceList();
            this.updateDisabled();
        });
    }

    bindStartScan() {
        click(this.d.getStartScan(), async event => {
            let scanner = this.getSelectedScanner();
            if (!scanner) {
                console.log("no scanner");
                return;
            }
            try {
                this.fireUseScanner(scanner);
                let file = await this.scan();
                this.itemList.addScan(file);
                this.updateDisabled();
            } finally {
                this.fireUnuseScanner(scanner);
            }
        });
    }

    bindReScan() {
        on(this.ele, "re-scan", async event => {
            event.stopPropagation();
            let item = event.detail;
            let scanner = this.getSelectedScanner();
            if (!scanner) {
                console.log("no scanner");
                return;
            }
            try {
                this.fireUseScanner(scanner);
                let file = await this.scan();
                await item.deleteScannedFile();
                item.setScannedFile(file);
                this.updateDisabled();
                await item.disp();
            } finally {
                this.fireUnuseScanner(scanner);
            }
        });
    }

    bindUpload() {
        click(this.d.getUpload(), async event => {
            this.prop.isUploading = true;
            this.prop.isBeforeUpload = false;
            this.updateDisabled();
            try {
                let ok = await this.itemList.upload();
                if (ok) {
                    let notice = new Notice("画像がアップロードされました。");
                    this.ele.parentElement.insertBefore(notice.ele, this.ele);
                    notice.autoClose(5);
                    this.fireDeleted();
                    this.ele.remove();
                    await this.itemList.deleteScannedFiles();
                }
            } finally {
                this.prop.isUploading = false;
                this.updateDisabled();
            }
        });
    }

    bindCancel() {
        click(this.d.getCancel(), async event => {
            if (this.itemList.isEmpty() || confirm("このスキャンをキャンセルしますか？")) {
                await this.itemList.deleteScannedFiles();
                this.fireDeleted();
                this.ele.remove();
            }
        });
    }

    bindItemDeleted() {
        on(this.d.getScannedItems(), "item-deleted", event => this.updateDisabled());
    }

    focus() {
        this.d.getSearchPatientText().focus();
    }

    updateDisabled() {
        let isScanning = this.prop.scannersInUse.includes(this.getSelectedScanner());
        let isUploading = this.prop.isUploading;
        let isBeforeUpload = this.prop.isBeforeUpload;
        enable(this.d.getSelectPatient(), !isScanning && !isUploading && isBeforeUpload);
        enable(this.d.getTagSelect(), !isScanning && !isUploading && isBeforeUpload);
        enable(this.d.getDeviceList(), !isScanning && !isUploading && isBeforeUpload);
        enable(this.d.getRefreshDeviceList(), !isScanning && !isUploading && isBeforeUpload);
        enable(this.d.getStartScan(), !isScanning && !isUploading && isBeforeUpload);
        enable(this.d.getUpload(), !isScanning && !isUploading && this.prop.patient &&
            !this.itemList.isEmpty());
        enable(this.d.getCancel(), !isScanning && !isUploading && isBeforeUpload
        );
        this.itemList.updateDisabled();
    }

    setPatient(patient) {
        this.prop.patient = patient;
        this.d.getSelectedPatientDisp().innerText = patientRep(patient);
    }

    async loadDeviceList() {
        let devices = await this.prop.printAPI.listScannerDevices();
        let select = this.d.getDeviceList();
        select.innerHTML = "";
        for (let device of devices) {
            let opt = document.createElement("option");
            opt.innerText = device.name;
            opt.value = device.deviceId;
            select.appendChild(opt);
        }
    }

    getSelectedTag() {
        return this.d.getTagSelect().value;
    }

    getSelectedScanner() {
        return this.d.getDeviceList().value;
    }

    async scan() {
        let deviceId = this.getSelectedScanner();
        if (deviceId == null) {
            throw new Error("スキャナーが見つかりません。");
        }
        let progress = this.d.getScanProgress();
        try {
            progress.innerText = "スキャンの準備中";
            let file = await this.prop.printAPI.scan(deviceId, pct => {
                progress.innerText = `${pct}%`;
            });
            progress.innerText = "";
            return file;
        } finally {
            progress.innerText = "";
        }
    }

}

async function searchPatient(rest, text) {
    text = text.trim();
    if (text === "") {
        return [];
    } else {
        return await rest.searchPatient(text);
    }
}

function patientToOption(patient) {
    let opt = document.createElement("option");
    opt.value = patient.patientId;
    opt.innerText = patientRep(patient);
    return opt;
}

function patientRep(patient) {
    if (patient) {
        let patientIdRep = ("" + patient.patientId).padStart(4, "0");
        return `(${patientIdRep}) ${patient.lastName}${patient.firstName}`;
    } else {
        return "";
    }
}

function setSearchResult(select, patients) {
    select.innerHTML = "";
    patients.forEach(patient => select.append(patientToOption(patient)));
    if (patients.length === 1) {
        select.size = 2;
    } else if (patients.length > 10) {
        select.size = 10;
    } else {
        select.size = patients.length;
    }
    select.scrollTop = 0;
    if (patients.length > 0) {
        select.querySelector("option").selected = true;
    }
}

async function getSelectedPatient(select, rest) {
    let patientId = select.value;
    if (!patientId) {
        return null;
    }
    return await rest.getPatient(patientId);
}

