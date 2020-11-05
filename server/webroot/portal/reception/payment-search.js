import {Widget} from "./widget2.js";
import {parseElement} from "../js/parse-node.js";
import {PaymentTable} from "./payment-table.js";

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
        this.table = new PaymentTable(map.searchResult);
        this.getCommandsElement().innerHTML = commandsTmpl;
        let cmap = parseElement(this.getCommandsElement());
        cmap.close.addEventListener("click", event => this.close());
    }

    async doRecentPayment(){
        console.log("doRecentPayment");
        let payments = await this.rest.listRecentPayment();
        this.table.clearItems();
        for(let p of payments){
            let {patient, payment, visit} = p;
            this.table.addItem(patient.patientId, `${patient.lastName}${patient.firstName}`,
                "", "");
        }
    }
}