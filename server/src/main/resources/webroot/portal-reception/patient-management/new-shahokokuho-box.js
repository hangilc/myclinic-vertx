import {createElementFrom} from "../js/create-element-from.js";
import {ShahokokuhoForm} from "../components/shahokokuho-form.js";
import {parseElement} from "../js/parse-node.js";

let tmpl = `
<div class="shahokokuho-box-new border border-warning rounded p-2 mb-2">
    <h6 class="mb-2">新規社保国保入力</h6>
    <div class="x-form mb-2"></div>
    <div class="text-right">
        <button class="btn btn-sm btn-primary x-enter">入力</button>
        <button class="btn btn-sm btn-secondary x-cancel">キャンセル</button>
    </div>
</div>
`;

export class NewShahokokuhoBox {
    constructor(patientId, rest){
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.form = new ShahokokuhoForm(null);
        this.form.setPatientId(patientId);
        this.map.form.appendChild(this.form.ele);
        this.map.enter.addEventListener("click", async event => await this.doEnter());
        this.map.cancel.addEventListener("click", event => this.ele.remove());
    }

    async doEnter(){
        let shahokokuhoOpt = this.form.get();
        if( shahokokuhoOpt.ok ){
            let shahokokuho = shahokokuhoOpt.value.shahokokuho;
            let edaban = shahokokuhoOpt.value.edaban;
            shahokokuho.shahokokuhoId = await this.rest.enterShahokokuho(shahokokuho);
            this.ele.dispatchEvent(new CustomEvent("shahokokuho-entered", {detail: shahokokuho}));
        } else {
            alert(shahokokuhoOpt.message);
        }
    }
}