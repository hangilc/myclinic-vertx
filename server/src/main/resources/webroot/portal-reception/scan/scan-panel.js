import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {ScanWidget} from "./scan-widget.js";

let tmpl = `
<div>
    <div class="form-inline align-middle mb-3">
        <div class="h3 mr-auto">スキャン</div>
        <button class="btn btn-secondary btn-sm x-new-scan">新規スキャン</button>
    </div>
    <div class="x-workarea"></div>
</div>
`;
export class ScanPanel {
    constructor(rest, printAPI) {
        this.rest = rest;
        this.printAPI = printAPI;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.newScan.addEventListener("click", async event => await this.addWidget());
    }

    async postConstruct() {
        await this.addWidget();
    }

    async reloadHook() {
        // nop
    }

    async addWidget(){
        let widget = new ScanWidget(this.rest, this.printAPI);
        await widget.refreshDeviceList();
        widget.status.updateUI();
        this.map.workarea.prepend(widget.ele);
        widget.focus();
        return widget;
    }

}
