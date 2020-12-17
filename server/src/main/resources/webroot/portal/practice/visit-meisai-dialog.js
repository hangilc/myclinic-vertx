import {Dialog} from "../../js/dialog.js";
import {parseElement} from "../../js/parse-node.js";
import {click} from "../../js/dom-helper.js";
import {createElementFrom} from "../../js/create-element-from.js";

let bodyTmpl = `
    <div class="x-sections"></div>
    <div>総点：<span class="x-total-ten"></span></div>
`;

let footerTmpl = `
    <button type="button" class="btn btn-secondary x-close">閉じる</button>
`;

let itemTmpl = `
    <div>
        <div class="x-title"></div>
        <div class="x-detail"></div>
    </div>
`;

let detailTmpl = `
    <div class="row">
        <div class="col-sm-2"></div>
        <div class="col-sm-4 x-detail-label"></div>
        <div class="col-sm-2 x-detail-ten"></div>
    </div>
`;

export class VisitMeisaiDialog extends Dialog {
    constructor(meisai) {
        super();
        this.setTitle("診療明細")
        this.getBody().innerHTML = bodyTmpl;
        this.bmap = parseElement(this.getBody());
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        this.setMeisai(meisai);
        click(fmap.close, event => this.close());
    }

    setMeisai(meisai) {
        for (let sect of meisai.sections) {
            let e = this.createSectionElement(sect);
            this.bmap.sections.append(e);
        }
        this.bmap.totalTen.innerText = meisai.totalTen;
        return this;
    }

    createSectionElement(section) {
        let e = createElementFrom(itemTmpl);
        let map = parseElement(e);
        map.title.innerText = section.label;
        for (let item of section.items) {
            let d = createElementFrom(detailTmpl);
            let dm = parseElement(d);
            dm.detailLabel.innerText = item.label;
            dm.detailTen.innerText = `${item.tanka}x${item.count}=${item.tanka * item.count}`;
            map.detail.append(d);
        }
        return e;
    }
}