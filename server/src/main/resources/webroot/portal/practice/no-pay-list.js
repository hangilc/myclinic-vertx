import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import * as kanjidate from "../../js/kanjidate.js";
import * as app from "./app.js";
import {on} from "../../js/dom-helper.js";

let itemTmpl = `
    <div>
        <span class="x-date"></span>
        <span class="x-charge"></span>
    </div>
`;

class Item {
    constructor(visit, charge){
        this.visit = visit;
        this.charge = charge;
        this.chargeValue = this.getChargeValue();
        this.visitId = visit.visitId;
        this.ele = createElementFrom(itemTmpl);
        this.ele.style.order = visit.visitId;
        this.map = parseElement(this.ele);
        this.map.date.innerText = kanjidate.sqldateToKanji(visit.visitedAt.substring(0, 10));
        this.map.charge.innerText = this.chargeRep(charge);
    }

    getVisitId(){
        return this.visitId;
    }

    getChargeValue(){
        let charge = this.charge;
        return charge ? charge.charge : 0;
    }

    chargeRep(charge){
        if( charge ){
            let valueRep = charge.charge.toLocaleString();
            return `${valueRep}円`;
        } else {
            return "（未請求）";
        }
    }

}

let managementTmpl = `
    <div>
        <a href="javascript:void(0)" class="x-disp">表示</a>
        <a href="javascript:void(0)" class="x-delete">削除</a>
    </div>
`;

class PdfMangement {
    constructor(prop, pdfToken){
        this.pdfToken = pdfToken;
        this.ele = createElementFrom(managementTmpl);
        this.map = parseElement(this.ele);
        this.map.disp.addEventListener("click", event => {
            let url = prop.rest.urlShowFileToken(pdfToken);
            window.open(url, "_blank");
        });
        this.map.delete.addEventListener("click", async event => {
            await prop.rest.deleteFile(pdfToken);
            this.ele.remove();
        });
    }
}

let tmpl = `
    <div class="practice-no-pay-list no-pay-list-listener mb-2 border rounded p-2">
        <div class="h5">未収リスト</div>
        <div class="x-list d-flex flex-column"></div>
        <div class="x-sum"></div>
        <div class="x-commands">
            <button class="btn btn-primary btn-sm x-receipt-pdf">領収書PDF</button>
            <button class="btn btn-secondary btn-sm x-batch-payment">会計</button>
            <a href="javascript:void(0)" class="x-close">閉じる</a>
        </div>
        <div class="x-receipt-workarea"></div>
    </div>
`;

export class NoPayList {
    constructor(){
        this.prop = app;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.items = [];
        on(this.ele, "update-ui", async event => this.updateUI());
        this.map.receiptPdf.addEventListener("click", async event => {
            let visitIds = this.items.map(item => item.visitId);
            let pdfToken = await this.prop.rest.createReceiptPdf(visitIds);
            let mgmt = new PdfMangement(prop, pdfToken);
            this.map.receiptWorkarea.innerHTML = "";
            this.map.receiptWorkarea.append(mgmt.ele);
        });
        this.map.batchPayment.addEventListener("click", async event => {
            if( !confirm("会計扱いにしますか？") ){
                return;
            }
            let paytime = kanjidate.nowAsSqldatetime();
            let payments = this.items.map(item => ({
                visitId: item.visit.visitId,
                amount: item.getChargeValue(),
                paytime
            }));
            await prop.rest.batchEnterPayment(payments);
            this.ele.dispatchEvent(new CustomEvent("payment-updated", {
                bubbles: true,
                detail: this.items.map(item => item.visit.visitId)
            }));
        });
        this.map.close.addEventListener("click", event => {
            this.ele.dispatchEvent(new Event("closed"));
            this.ele.remove();
        });
    }

    async updateUI(){
        const visitIds = app.getNoPayList();
        console.log(visitIds);
        visitIds.forEach(visitId => {
            for(const item of this.items){
                if( item.getVisitId() === visitId ){
                    return;
                }
            }
            this.add(visitId);
        });
    }

    async add(visitId){
        let visit = await this.prop.rest.getVisit(visitId);
        if( !this.confirmSamePatient(visit.patientId) ){
            alert("別の患者の会計です。");
            return;
        }
        let charge = await this.prop.rest.getCharge(visitId);
        let item = new Item(visit, charge);
        this.items.push(item);
        this.map.list.append(item.ele);
        this.updateSum();
    }

    confirmSamePatient(patientId){
        if( this.items.length > 0 ){
            return this.items[0].visit.patientId === patientId;
        } else {
            return true;
        }
    }

    updateSum(){
        let sum = 0;
        for(let item of this.items){
            sum += item.chargeValue;
        }
        this.map.sum.innerText = `合計 ${sum.toLocaleString()}円`;
    }

}