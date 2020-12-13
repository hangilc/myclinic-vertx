import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {ScanWidget} from "./scan-widget.js";
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
    constructor(rest, printAPI) {
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

    async postConstruct() {
        await this.addWidget();
    }

    async reloadHook() {

    }

    bindNewScan() {
        this.map.newScan.addEventListener("click", async event => await this.addWidget());
    }

    bindWidgetDeleted() {
        on(this.ele, "widget-deleted", async event => {
            let widget = event.detail;
            this.widgets = this.widgets.filter(w => w !== widget);
            console.log("widgets", this.widgets);
            if( this.widgets.length === 0 ){
                await this.addWidget();
            }
        });
    }

    bindUseScanner() {
        on(this.ele, "use-scanner", event => {
            event.stopPropagation();
            let scanner = event.detail;
            this.prop.scannersInUse.push(scanner);
            this.widgets.forEach(w => w.updateDisabled());
        });
    }

    bindUnuseScanner() {
        on(this.ele, "unuse-scanner", event => {
            event.stopPropagation();
            let scanner = event.detail;
            this.prop.scannersInUse = this.prop.scannersInUse.filter(s => s !== scanner);
            this.widgets.forEach(w => w.updateDisabled());
        })
    }

    async addWidget(){
        let widget = new ScanWidget(this.prop);
        await widget.postConstruct();
        this.widgets.push(widget);
        this.map.workarea.prepend(widget.ele);
        widget.focus();
    }

}
