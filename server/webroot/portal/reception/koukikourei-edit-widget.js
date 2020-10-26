import {Widget} from "./widget2.js";
import {KoukikoureiForm} from "./koukikourei-form.js";
import {RadioInput} from "./radio-input.js";
import {DateInput} from "./date-input.js";

let commandTmpl = `
    <button type="button" class="x-enter btn btn-secondary">入力</button>
    <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
`;

export class KoukikoureiEditWidget extends Widget {
    constructor(koukikourei, rest){
        super();
        this.setTitle("後期高齢保険編集");
        this.koukikourei = koukikourei;
        this.rest = rest;
        this.form = new KoukikoureiForm(this.getContentElement());
        this.form.set(koukikourei);
        let cmap = this.setCommands(commandTmpl);
        cmap.enter.addEventListener("click", async event => await this.doEnter());
        cmap.close.addEventListener("click", event => this.close());
    }

    set(koukikourei){
        super.set();
        this.koukikourei = koukikourei;
        this.form.set(koukikourei);
        return this;
    }

    onUpdated(cb){
        this.ele.addEventListener("updated", event => cb(event.detail));
    }

    async doEnter(){
        let koukikoureiId = this.koukikourei.koukikoureiId;
        let patientId = this.koukikourei.patientId;
        if( !(koukikoureiId > 0) ){
            alert("Invalid koukikoureiId");
            return;
        }
        if( !(patientId > 0) ){
            alert("Invalid patientId");
            return;
        }
        let data = this.form.get(koukikoureiId, patientId);
        if( !data ){
            let err = this.form.getError();
            alert(err);
            return;
        }
        await this.rest.updateKoukikourei(data);
        let updated = await this.rest.getKoukikourei(koukikoureiId);
        this.ele.dispatchEvent(new CustomEvent("updated", { detail: updated }));
    }
}