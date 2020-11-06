import {Widget} from "./widget2.js";
import {parseElement} from "../js/parse-node.js";
import {PaymentTable} from "./payment-table.js";
import * as kanjidate from "../js/kanjidate.js";
import {openPrintDialog} from "../js/print-dialog.js";
import {MeisaiDialog} from "./meisai-dialog.js";

let tmpl = `
    <div class="form-inline">
        <button class="x-recent-payment btn btn-primary mr-4">最近の会計</button>
        患者番号：<input type="text" class="x-search-text form-control"/>
        <button class="x-search btn btn-secondary ml-2">検索</button>
    </div>
    <div class="x-search-result search-result"></div>
`;

let commandsTmpl = `
    <button class="x-re-issue-receipt btn btn-primary">領収書再発行</button>
    <button class="x-show-meisai-detail btn btn-secondary ml-2">明細情報表示</button>
    <button class="x-close btn btn-secondary ml-2">閉じる</button>
`;

export class PaymentSearch extends Widget {
    constructor(rest){
        super();
        this.ele.classList.add("payment-search");
        this.rest = rest;
        this.setTitle("会計検索");
        this.getContentElement().innerHTML = tmpl;
        let map = parseElement(this.getContentElement());
        map.recentPayment.addEventListener("click", async event => this.doRecentPayment());
        map.search.addEventListener("click", async event => this.doSearch());
        this.map = map;
        this.table = new PaymentTable(map.searchResult);
        this.getCommandsElement().innerHTML = commandsTmpl;
        let cmap = parseElement(this.getCommandsElement());
        cmap.reIssueReceipt.addEventListener("click", async event => this.doReIssueReceipt());
        cmap.showMeisaiDetail.addEventListener("click", async event => this.doShowMeisaiDetail());
        cmap.close.addEventListener("click", event => this.close());
    }

    async doSearch(){
        let text = this.map.searchText.value;
        let patientId = parseInt(text);
        if( !patientId ){
            alert("患者番号の入力が適切でありません。");
            return;
        }
        let list = await this.rest.listVisitIdByPatient(patientId);
        console.log(list);
    }

    async doShowMeisaiDetail(){
        let visitId = this.table.getSelectedData();
        if( visitId ){
            let meisai = await this.rest.getMeisai(visitId);
            let visit = await this.rest.getVisit(visitId);
            let patient = await this.rest.getPatient(visit.patientId);
            let charge = await this.rest.getCharge(visitId);
            let dialog = new MeisaiDialog(meisai, patient, visit, charge);
            await dialog.open();
        }
    }

    async doReIssueReceipt(){
        let visitId = this.table.getSelectedData();
        if( visitId ) {
            let meisai = await this.rest.getMeisai(visitId);
            let visit = await this.rest.getVisit(visitId);
            let patient = await this.rest.getPatient(visit.patientId);
            let charge = await this.rest.getCharge(visitId);
            let clinicInfo = await this.rest.getClinicInfo();
            let req = {
                meisai,
                patient,
                visit,
                charge: charge == null ? null : charge.charge,
                clinicInfo: clinicInfo
            }
            let ops = await this.rest.receiptDrawer(req);
            await openPrintDialog("領収書", null, [ops], "reception", "receipt");
        }
    }

    async doRecentPayment(){
        let payments = await this.rest.listRecentPayment();
        this.table.clearItems();
        for(let p of payments){
            let {patient, payment, visit} = p;
            let at = kanjidate.sqldatetimeToKanji(visit.visitedAt,
                {padZero: true, omitSecond:true, sep: " "})
            this.table.addItem(patient.patientId, `${patient.lastName}${patient.firstName}`,
                `${payment.amount.toLocaleString()}円`, at, visit.visitId);
        }
    }
}