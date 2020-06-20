import {Dialog} from "./dialog.js";
import {parseElement} from "../js/parse-element.js";

export class CashierDialog extends Dialog {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.sectionsElement = map.sections;
        this.totalTenElement = map.totalTen;
        this.printReceiptElement = map.printReceipt;
        this.endElement = map.end;
        this.cancelElement = map.cancel;
        this.itemTemplateHtml = map.itemTemplate.html();
        this.detailTemplateHtml = map.detailTemplate.html();
    }

    init() {
        super.init();
        this.printReceiptElement.on("click", event => this.doPrintReceipt());
        this.endElement.on("click", event => this.doEnd());
        this.cancelElement.on("click", event => this.close());
        return this;
    }

    set(meisai, visitId) {
        super.set();
        this.meisai = meisai;
        this.visitId = visitId;
        for (let sect of meisai.sections) {
            let e = this.createSectionElement(sect);
            this.sectionsElement.append(e);
        }
        this.totalTenElement.text(meisai.totalTen);
        return this;
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
        let charge = this.meisai.charge;
        let paytime = kanjidate.nowAsSqldatetime();
        console.log(charge, visitId, paytime);
        // await this.rest.finishCharge(visitId, charge, paytime);
    }

}
