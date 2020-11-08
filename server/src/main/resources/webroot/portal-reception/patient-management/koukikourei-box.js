import {KoukikoureiDisp} from "../components/koukikourei-disp.js";
import {KoukikoureiForm} from "../components/koukikourei-form.js";
import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";

let tmpl = `
<div class="border border-warning rounded p-2 mb-2">
    <h6 class="x-title mb-2">後期高齢保険</h6>
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

export class KoukikoureiBox {
    constructor(koukikourei, rest) {
        this.koukikourei = koukikourei;
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        this.ele.classList.add(`koukikourei-box-${koukikourei.koukikoureiId}`);
        this.map = parseElement(this.ele);
        this.showDisp();
    }

    showDisp() {
        let disp = new KoukikoureiDisp(this.koukikourei);
        this.map.content.innerHTML = "";
        this.map.content.appendChild(disp.ele);
        this.map.commands.innerHTML = dispCommandsTmpl;
        let cmap = parseElement(this.map.commands);
        cmap.edit.addEventListener("click", event => this.showForm());
        cmap.close.addEventListener("click", event => this.ele.remove());
    }

    showForm(){
        let form = new KoukikoureiForm(this.koukikourei);
        this.map.content.innerHTML = "";
        this.map.content.append(form.ele);
        this.map.commands.innerHTML = formCommandsTmpl;
        let cmap = parseElement(this.map.commands);
        cmap.enter.addEventListener("click", async event => {
            let valueOpt = form.get();
            if( valueOpt.ok ){
                let value = valueOpt.value
                await this.rest.updateKoukikourei(value);
                this.ele.dispatchEvent(new Event("updated"));
                this.koukikourei = value;
                this.showDisp();
            } else {
                alert(valueOpt.message);
            }
        });
        cmap.cancel.addEventListener("click", event => this.showDisp());
    }
}
