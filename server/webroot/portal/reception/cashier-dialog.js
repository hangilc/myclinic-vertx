import {Dialog} from "./dialog.js";
import {parseElement} from "../js/parse-element.js";
import {compareBy} from "../js/general-util.js";

export class CashierDialog extends Dialog {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.sectionsElement = map.sections;
        this.summaryElement = map.summary;
        this.printReceiptElement = map.printReceipt;
        this.endElement = map.end;
        this.cancelElement = map.cancel;
        this.itemTemplateHtml = map.itemTemplate.html();
        this.detailTemplateHtml = map.detailTemplate.html();
        this.paymentsElement = map.payments;
        this.chargeElement = map.charge;
    }

    init() {
        super.init();
        this.printReceiptElement.on("click", event => this.doPrintReceipt());
        this.endElement.on("click", event => this.doEnd());
        this.cancelElement.on("click", event => this.close());
        return this;
    }

    set(meisai, visitId, chargeValue, payments) {
        super.set();
        this.meisai = meisai;
        this.visitId = visitId;
        this.chargeValue = chargeValue;
        for (let sect of meisai.sections) {
            let e = this.createSectionElement(sect);
            this.sectionsElement.append(e);
        }
        this.summaryElement.text(this.createSummary(meisai));
        let chargeRep = chargeValue.toLocaleString();
        let lastPayment = this.getLastPayment(payments);
        if( lastPayment !== null ){
            this.paymentsElement.text(`支払い済額：${lastPayment}円`);
            this.chargeElement.text(`請求額：${chargeValue} - ${lastPayment} = ${chargeValue - lastPayment}円 `);
        } else {
            this.chargeElement.text(`請求額：${chargeValue}円 `);
        }
        return this;
    }

    getLastPayment(payments){
        if( payments && payments.length > 0 ){
            payments.sort(compareBy("-paytime"));
            return payments[0].amount;
        } else {
            return null;
        }
    }

    createSummary(meisai){
        return `総点：${meisai.totalTen}点, 負担割：${meisai.futanWari}割`;
    }

    createSectionElement(section) {
        let e = $(this.itemTemplateHtml);
        let map = parseElement(e);
        map.title.text(section.label);
        for (let item of section.items) {
            let d = $(this.detailTemplateHtml);
            let dm = parseElement(d);
            dm.detailLabel.text(item.label);
            let ten = `${item.tanka}x${item.count}=${item.tanka * item.count}`;
            dm.detailTen.text(ten);
            map.detail.append(d);
        }
        return e;
    }

    doPrintReceipt(){
        alert("Not implemented");
    }

    async doEnd(){
        let visitId = this.visitId;
        let charge = this.chargeValue;
        let paytime = kanjidate.nowAsSqldatetime();
        await this.rest.finishCharge(visitId, charge, paytime);
        this.close(true);
    }

}
