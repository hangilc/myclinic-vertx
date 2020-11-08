import {KouhiDisp} from "../components/kouhi-disp.js";
import {KouhiForm} from "../components/kouhi-form.js";
import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";

let tmpl = `
<div class="border border-warning rounded p-2 mb-2">
    <h6 class="x-title mb-2">公費負担</h6>
    <div class="x-content mb-2"></div>
    <div class="x-commands text-right">
    </div>
</div>
`;

let dispCommandsTmpl = `
    <button class="btn btn-sm btn-primary x-edit">編集</button>
    <button class="btn btn-sm btn-secondary x-close">閉じる</button>
`;

let formCommandsTmpl = `
    <button class="btn btn-sm btn-primary x-enter">入力</button>
    <button class="btn btn-sm btn-secondary x-cancel">キャンセル</button>
`;

export class KouhiBox {
    constructor(kouhi, rest) {
        this.kouhi = kouhi;
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        this.ele.classList.add(`kouhi-box-${kouhi.kouhiId}`);
        this.map = parseElement(this.ele);
        this.showDisp();
    }

    showDisp() {
        let disp = new KouhiDisp(this.kouhi);
        this.map.content.innerHTML = "";
        this.map.content.appendChild(disp.ele);
        this.map.commands.innerHTML = dispCommandsTmpl;
        let cmap = parseElement(this.map.commands);
        cmap.edit.addEventListener("click", event => this.showForm());
        cmap.close.addEventListener("click", event => this.ele.remove());
    }

    showForm(){
        let form = new KouhiForm(this.kouhi);
        this.map.content.innerHTML = "";
        this.map.content.append(form.ele);
        this.map.commands.innerHTML = formCommandsTmpl;
        let cmap = parseElement(this.map.commands);
        cmap.enter.addEventListener("click", async event => {
            let valueOpt = form.get();
            if( valueOpt.ok ){
                let value = valueOpt.value
                await this.rest.updateKouhi(value);
                this.ele.dispatchEvent(new Event("updated"));
                this.kouhi = value;
                this.showDisp();
            } else {
                alert(valueOpt.message);
            }
        });
        cmap.cancel.addEventListener("click", event => this.showDisp());
    }
}
