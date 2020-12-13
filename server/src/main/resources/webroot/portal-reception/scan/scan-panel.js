import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {ScanWidget} from "./scan-widget.js";
import {ScannedItem} from "./scanned-item.js";
import {on} from "../../js/dom-helper.js";

let tmpl = `
<div>
    <div class="form-inline align-middle mb-3">
        <div class="h3 mr-auto">スキャン</div>
        <button class="btn btn-secondary btn-sm x-new-scan">新規スキャン</button>
    </div>
    <div class="x-workarea"></div>
    <div class="d-none x-unfinished-wrapper">
        <div class="h4">未完了のアップロード</div>
        <div class="x-unfinished-workarea"></div>
    </div>
</div>
`;

export class ScanPanel {
    constructor(rest, printAPI){
        this.prop = Object.create({
            rest,
            printAPI,
            scannersInUse: []
        });
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.widgets = [];
        this.bindNewScan();
        this.bindWidgetDeleted();
        this.bindUseScanner();
        this.bindUnuseScanner();
    }

    async postConstruct(){

    }

    async reloadHook(){

    }

    bindNewScan(){
        this.map.newScan.addEventListener("click", async event => {
            let widget = new ScanWidget(this.prop);
            await widget.postConstruct();
            this.widgets.push(widget);
            this.map.workarea.prepend(widget.ele);
            widget.focus();
        });
    }

    bindWidgetDeleted(){
        this.ele.addEventListener("widget-deleted", event => {
            let widget = event.detail;
            this.widgets = this.widgets.filter(w => w !== widget);
        });
    }

    bindUseScanner(){
        on(this.ele, "use-scanner", event => {
            let scanner = event.detail;
            this.prop.scannersInUse.push(scanner);
            this.widgets.forEach(w => w.updateDisabled());
        });
    }

    bindUnuseScanner(){
        on(this.ele, "unuse-scanner", event => {
            let scanner = event.detail;
            this.prop.scannersInUse = this.prop.scannersInUse.filter(s => s !== scanner);
            this.widgets.forEach(w => w.updateDisabled());
        })
    }

}

export class ScanPanelOrig {
    constructor(rest, printAPI) {
        this.rest = rest;
        this.printAPI = printAPI;
        this.scannersInUse = [];
        this.widgets = [];
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.setupBindings();
    }

    setupBindings(){
        this.map.newScan.addEventListener("click",
            async event => await this.addWidget());
        this.ele.addEventListener("use-scanner", event => {
            event.stopPropagation();
            this.scannersInUse.push(event.detail);
            this.widgets.forEach(w => {
                w.setScannersInUse(this.scannersInUse);
                w.updateDisabled()
            });
        });
        this.ele.addEventListener("unuse-scanner", event => {
            event.stopPropagation();
            let scanner = event.detail;
            this.scannersInUse = this.scannersInUse.filter(s => s !== scanner);
            this.widgets.forEach(w => {
                w.setScannersInUse(this.scannersInUse);
                w.updateDisabled()
            });
        });
    }

    async postConstruct() {
        await this.addWidget();
        //await this.probeForUnfinished();
    }

    async reloadHook() {
        //await this.probeForUnfinished();
    }

    async addWidget() {
        let widget = new ScanWidgetOrig(this.rest, this.printAPI);
        await widget.postConstruct();
        widget.scannersInUse = this.scannersInUse;
        widget.updateDisabled();
        this.map.workarea.prepend(widget.ele);
        widget.ele.addEventListener("remove", async event => {
            widget.ele.remove();
            if (this.ele.querySelector(".scan-widget") == null) {
                await this.addWidget();
            }
        });
        widget.focus();
        this.widgets.push(widget);
        return widget;
    }

    async probeForUnfinished(){
        let list = await this.printAPI.listUploadJob();
        this.map.unfinishedWorkarea.innerHTML = "";
        if( list.length > 0 ){
            for(let job of list){
                let jobContent = await this.printAPI.getUploadJob(job);
                let patient = await this.rest.getPatient(jobContent.patientId);
                let items = jobContent.uploadFiles.map(f => {
                    let item = new ScannedItem(f.scannedFileName, this.printAPI, this.rest);
                    if(f.uploadFileName){
                        item.setUpload(f.uploadFileName, patient.patientId);
                    }
                    return item;
                });
                let widget = new ScanWidget(this.rest, this.printAPI, patient, items, job);
                this.map.unfinishedWorkarea.append(widget.ele);
            }
            this.map.unfinishedWrapper.classList.remove("d-none");
        } else {
            this.map.unfinishedWrapper.classList.remove("d-none");
        }
    }

}

