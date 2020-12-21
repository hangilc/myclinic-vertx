import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";

let itemTmpl = `
    <div>
        <span class="x-date"></span>
        <span class="x-charge"></span>
    </div>
`;

class Item {
    constructor(visit, charge){
        this.chargeValue = this.getChargeValue(charge);
        this.visitId = visit.visitId;
        this.ele = createElementFrom(itemTmpl);
        this.map = parseElement(this.ele);
        this.map.date.innerText = kanjidate.sqldateToKanji(visit.visitedAt.substring(0, 10));
        this.map.charge.innerText = this.chargeRep(charge);
    }

    getChargeValue(charge){
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
    <div class="mb-2 border rounded p-2">
        <div class="h5">未収リスト</div>
        <div class="x-list"></div>
        <div class="x-sum"></div>
        <div class="x-commands">
            <button class="btn btn-primary btn-sm x-receipt-pdf">領収書PDF</button>
            <a href="javascript:void(0)" class="x-close">閉じる</a>
        </div>
        <div class="x-receipt-workarea"></div>
    </div>
`;

export class NoPayList {
    constructor(prop){
        this.prop = prop;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.items = [];
        this.map.receiptPdf.addEventListener("click", async event => {
            let visitIds = this.items.map(item => item.visitId);
            let pdfToken = await this.prop.rest.createReceiptPdf(visitIds);
            let mgmt = new PdfMangement(prop, pdfToken);
            this.map.receiptWorkarea.innerHTML = "";
            this.map.receiptWorkarea.append(mgmt.ele);
        });
        this.map.close.addEventListener("click", event => {
            this.ele.dispatchEvent(new Event("closed"));
            this.ele.remove();
        });
    }

    async add(visitId){
        let visit = await this.prop.rest.getVisit(visitId);
        let charge = await this.prop.rest.getCharge(visitId);
        let item = new Item(visit, charge);
        this.items.push(item);
        this.map.list.append(item.ele);
        this.updateSum();
    }

    updateSum(){
        let sum = 0;
        for(let item of this.items){
            sum += item.chargeValue;
        }
        this.map.sum.innerText = `合計 ${sum.toLocaleString()}円`;
    }
}