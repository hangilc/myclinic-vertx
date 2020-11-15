import {parseElement} from "../js/parse-node.js";
import {WqueueTable} from "./wqueue-table.js";
import {CashierDialog} from "./cashier-dialog.js";

let tmpl = `
<div>
    <div class="mb-3 form-inline">
        <div class="h3">会計</div>
    </div>
    <div class="x-wqueue-table"></div>
    <div>
        <button class="btn btn-secondary x-refresh">更新</button>
    </div>
    <div class="x-workarea pt-2"></div>
</div>
`;

export class MainPanel {
    constructor(ele, rest) {
        ele.innerHTML = tmpl;
        this.rest = rest;
        this.map = parseElement(ele);
        this.wqueueTable = new WqueueTable();
        this.wqueueTable.ele.addEventListener("wq-cashier", async event => {
            event.stopPropagation();
            let visitId = event.detail;
            await this.doCashier(visitId);
        });
        this.wqueueTable.ele.addEventListener("wq-delete", async event => {
            event.stopPropagation();
            let visitId = event.detail;
            await this.doDelete(visitId);
        });
        this.map.wqueueTable.appendChild(this.wqueueTable.ele);
        this.map.refresh.addEventListener("click", async event => await this.reloadHook());
    }

    async reloadHook(){
        let wqList = await this.rest.listWqueueFull();
        let tab = this.wqueueTable;
        tab.clear();
        wqList.forEach(wq => tab.addRow(wq));
    }

    async doCashier(visitId){
        let dialog = new CashierDialog(this.rest);
        await dialog.init(visitId);
        dialog.ele.addEventListener("cashier-done", async event => await this.reloadHook());
        await dialog.open();
    }

    async doDelete(visitId){
        if( confirm("この受付を削除しますか？") ){
            await this.rest.deleteVisitFromReception(visitId);
            await this.reloadHook();
        }
    }
}