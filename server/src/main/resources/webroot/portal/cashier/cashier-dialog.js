import {Dialog} from "../../js/dialog.js";
import {parseElement} from "../../js/parse-node.js";
import {click} from "../../js/dom-helper.js";
import {nowAsSqldatetime} from "../../js/kanjidate.js";

let bodyTmpl = `
    <pre class="x-detail"></pre>
    <div class="x-summary"></div>
    <div class="x-value"></div>
`;

let footerTmpl = `
    <button class="btn btn-link x-enter">会計終了</button>
    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
    <button type="button" class="btn btn-primary x-no-pay">未収終了</button>
`;

export class CashierDialog extends Dialog {
    constructor(prop, wqueueFull, meisai, charge){
        super();
        this.setTitle(this.createTitle(wqueueFull.patient));
        this.getBody().innerHTML = bodyTmpl;
        let bmap = parseElement(this.getBody());
        bmap.detail.innerHTML = meisai.sections.map(sect => {
            return `${sect.label}：${sect.sectionTotalTen.toLocaleString()} 点`;
        }).join("\n");
        bmap.summary.innerText = `総点：${meisai.totalTen.toLocaleString()} 点、負担割：${meisai.futanWari}割`;
        bmap.value.innerHTML = `請求額：${charge.charge.toLocaleString()} 円`;
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.enter, async event => {
            await prop.rest.finishCharge(
                wqueueFull.visit.visitId,
                charge.charge,
                nowAsSqldatetime());
            this.close(true);
        });
        click(fmap.cancel, event => this.close(null));
        click(fmap.noPay, async event => {
            await prop.rest.finishCharge(
                wqueueFull.visit.visitId,
                0,
                nowAsSqldatetime());
            this.close(true);
        });
    }

    createTitle(patient){
        let name = `${patient.lastName}${patient.firstName}`;
        let yomi = `${patient.lastNameYomi}${patient.firstNameYomi}`;
        return `会計：（${patient.patientId}）${name}（${yomi}）`;
    }
}