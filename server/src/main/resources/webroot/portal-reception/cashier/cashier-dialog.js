import {Dialog} from "../components/dialog.js";
import {parseElement} from "../js/parse-node.js";
import {MeisaiDisp} from "../components/meisai-disp.js";
import {PrintReceiptDialog} from "./print-receipt-dialog.js";

let bodyTmpl = `
<div class="x-detail mb-2"></div>
<div class="x-summary">
    <div class="x-info mb-2"></div>
    <div class="x-charge h5 font-weight-bold text-primary mb-0"></div>
</div>
`;

let commandsTmpl = `
<button class="btn btn-primary x-print-receipt">領収書印刷</button>
<button class="btn btn-secondary x-finish">会計終了</button>
<button class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class CashierDialog extends Dialog {
    constructor(rest){
        super();
        this.rest = rest;
        this.getBody().innerHTML = bodyTmpl;
        this.map = parseElement(this.getBody());
        this.getFooter().innerHTML = commandsTmpl;
        let cmap = parseElement(this.getFooter());
        cmap.printReceipt.addEventListener("click", async event => await this.doPrintReceipt());
        cmap.finish.addEventListener("click", async event => await this.doFinish());
        cmap.cancel.addEventListener("click", event => this.close(undefined));
    }

    async init(visitId){
        this.visitId = visitId;
        let rest = this.rest;
        let visit = this.visit = await rest.getVisit(visitId);
        let patient = this.patient = await rest.getPatient(visit.patientId);
        let title = `会計：（${patient.patientId}） ${patientName(patient)} （${patientNameYomi(patient)}）`;
        this.setTitle(title);
        let meisai = this.meisai = await rest.getMeisai(visitId);
        let charge = this.charge = await rest.getCharge(visitId);
        let meisaiDisp = new MeisaiDisp(meisai);
        this.map.detail.appendChild(meisaiDisp.ele);
        this.map.info.innerText = meisaiInfo(meisai);
        this.map.charge.innerText = meisaiCharge(charge);
        console.log(meisai);
        console.log(charge);
    }

    async getReceiptOps(){
        let clinicInfo = this.rest.getClinicInfo();
        let chargeValue = this.charge == null ? 0 : this.charge.charge;
        let req = {
            meisai: this.meisai,
            patient: this.patient,
            visit: this.visit,
            charge: chargeValue,
            clinicInfo: clinicInfo
        };
        return await this.rest.receiptDrawer(req);
    }

    async doPrintReceipt(){
        if( !(this.visitId > 0) ){
            console.log("visitId not specified");
            return;
        }
        let ops = await this.getReceiptOps();
        let printDialog = new PrintReceiptDialog(ops);
        $(this.ele).modal("hide");
        await printDialog.open();
        $(this.ele).modal("show");
    }

    async doFinish(){
        if( !(this.visitId > 0) ){
            console.log("visitId not specified");
            return;
        }
        this.close(true);
    }
}

function patientName(patient) {
    return `${patient.lastName}${patient.firstName}`
}

function patientNameYomi(patient){
    return `${patient.lastNameYomi}${patient.firstNameYomi}`
}

function meisaiInfo(meisai){
    let parts = [
        `総点：${meisai.totalTen}点`,
        `負担割：${meisai.futanWari}割`
    ];
    return parts.join("、");
}

function meisaiCharge(charge){
    let c = charge ? charge.charge : 0;
    return `請求額：${c.toLocaleString()}円`;
}
