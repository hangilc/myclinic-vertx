import {Dialog} from "../components/dialog.js";
import {parseElement} from "../js/parse-node.js";
import {MeisaiDisp} from "../components/meisai-disp.js";

let bodyTmpl = `
<div class="x-detail"></div>
<div class="x-summary"></div>
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
        let visit = await rest.getVisit(visitId);
        let patient = await rest.getPatient(visit.patientId);
        let title = `会計：（${patient.patientId}） ${patientName(patient)} （${patientNameYomi(patient)}）`;
        this.setTitle(title);
        let meisai = await rest.getMeisai(visitId);
        let charge = await rest.getCharge(visitId);
        let meisaiDisp = new MeisaiDisp(meisai);
        this.map.detail.appendChild(meisaiDisp.ele);
        console.log(meisai);
        console.log(charge);
    }

    async doPrintReceipt(){
        if( !(this.visitId > 0) ){
            console.log("visitId not specified");
            return;
        }
        this.close(true);
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
