import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {ScanWidget} from "./scan-widget.js";
import {ScannedItem} from "./scanned-item.js";

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
            this.widgets.forEach(w => w.updateUI(this.scannersInUse));
        });
        this.ele.addEventListener("unuse-scanner", event => {
            event.stopPropagation();
            let scanner = event.detail;
            this.scannersInUse = this.scannersInUse.filter(s => s !== scanner);
            this.widgets.forEach(w => w.updateUI(this.scannersInUse));
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
        let widget = new ScanWidget(this.rest, this.printAPI);
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

