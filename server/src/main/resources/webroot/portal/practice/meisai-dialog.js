import {Dialog} from "../../js/dialog2.js"
import {parseElement} from "../../js/parse-node.js";
import {click, show, hide} from "../../js/dom-helper.js";

let bodyTmpl = `
    <pre class="x-items"></pre>
    <div class="x-summary-part"></div>
    <div class="mb-2 x-value-wrapper">
        <span class="x-value"></span> 
        <a href="javascript:void(0)" class="x-modify-charge-button">変更</a>
    </div>
    <div class="d-none form-inline x-modify-charge-workarea">
        <input type="text" class="form-control mr-2 x-modify-charge-input"/>
        <span class="mr-2">円</span>
        <button class="btn btn-primary btn-sm mr-2 x-modify-charge-enter">入力</button>
        <button class="btn btn-secondary btn-sm x-modify-charge-cancel">キャンセル</button>
    </div>
`;

let footerTmpl = `
    <button type="button" class="btn btn-primary x-enter">入力</button>
    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class MeisaiDialog extends Dialog {
    constructor(meisai) {
        super();
        this.meisai = Object.assign({}, meisai);
        this.setTitle("会計");
        this.getBody().innerHTML = bodyTmpl;
        let bmap = this.bmap = parseElement(this.getBody());
        click(bmap.modifyChargeButton, event => {
            show(bmap.modifyChargeWorkarea);
            bmap.modifyChargeInput.focus();
        });
        click(bmap.modifyChargeEnter, event => this.doModifyCharge());
        click(bmap.modifyChargeCancel, event => hide(bmap.modifyChargeWorkarea));
        this.setItems();
        this.setSummary();
        this.setValue();
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.enter, event => this.close(this.meisai));
        click(fmap.cancel, event => this.close());
    }

    setItems() {
        let e = this.bmap.items;
        let meisai = this.meisai;
        e.innerText = meisai.sections.map(sect => {
            return `${sect.label}：${sect.sectionTotalTen.toLocaleString()} 点`;
        }).join("\n");
    }

    setSummary(){
        let e = this.bmap.summaryPart;
        let meisai = this.meisai;
        e.innerText = `総点：${meisai.totalTen.toLocaleString()} 点、負担割：${meisai.futanWari}割`;
    }

    setValue(){
        let e = this.bmap.value;
        let meisai = this.meisai;
        e.innerText = `請求額：${meisai.charge.toLocaleString()} 円`
    }

    doModifyCharge(){
        let chargeInput = this.bmap.modifyChargeInput.value.trim();
        let charge = parseInt(chargeInput);
        if( !(charge >= 0) ){
            alert("請求額の入力が不適切です。");
            return;
        }
        this.meisai.charge = charge;
        this.setValue();
        hide(this.bmap.modifyChargeWorkarea);
    }
}

