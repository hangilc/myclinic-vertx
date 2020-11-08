import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {KoukikoureiForm} from "../components/koukikourei-form.js";

let tmpl = `
<div class="koukikourei-box-new border border-warning rounded p-2 mb-2">
    <h6 class="mb-2">新規後期高齢入力</h6>
    <div class="x-form mb-2"></div>
    <div class="text-right">
        <button class="btn btn-sm btn-primary x-enter">入力</button>
        <button class="btn btn-sm btn-secondary x-cancel">キャンセル</button>
    </div>
</div>
`;


export class NewKoukikoureiBox {
    constructor(patientId, rest){
        this.patientId = patientId;
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        let form = new KoukikoureiForm();
        form.setPatientId(patientId);
        this.form = form;
        let map = parseElement(this.ele);
        map.form.append(form.ele);
        map.enter.addEventListener("click", async event => this.doEnter());
        map.cancel.addEventListener("click", event => this.ele.remove());
    }

    async doEnter(){
        let opt = this.form.get();
        if( opt.ok ){
            let value = opt.value;
            let koukikoureiId = await this.rest.enterKoukikourei(value);
            this.ele.dispatchEvent(new Event("koukikourei-entered"));
        } else {
            alert(opt.message);
        }
    }
}