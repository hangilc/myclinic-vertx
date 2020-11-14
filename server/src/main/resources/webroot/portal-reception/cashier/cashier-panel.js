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

export class CashierPanel {
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
        await dialog.open();
        // let rest = this.rest;
        // let meisai = await rest.getMeisai(visitId);
        // let charge = await rest.getCharge(visitId);
        // let dialog = chargeDialog;
        // dialog.title = `会計：（${wq.patient.patientId}）${name}（${yomi}）`;
        // dialog.detail = meisai.sections.map(sect => {
        //     return `${sect.label}：${sect.sectionTotalTen.toLocaleString()} 点`;
        // }).join("\n");
        // dialog.summary = `総点：${meisai.totalTen.toLocaleString()} 点、負担割：${meisai.futanWari}割`;
        // dialog.value = `請求額：${charge.charge.toLocaleString()} 円`;
        // dialog.setOnEnter(async () => {
        //     await rest.finishCharge(visitId, charge.charge, moment());
        //     dialog.hide();
        //     update();
        // });
        // dialog.show();
    }
}