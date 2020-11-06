import {Widget} from "./widget2.js";
import {KouhiForm} from "./kouhi-form.js";
import {DateInput} from "./date-input.js";
import {RadioInput} from "./radio-input.js";

let commandsTmpl = `
    <button type="button" class="x-enter btn btn-secondary">入力</button>
    <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
`;

export class KouhiEditWidget extends Widget {
    constructor(kouhi, rest){
        super();
        this.kouhi = kouhi;
        this.rest = rest;
        this.setTitle("公費負担編集");
        this.form = new KouhiForm(this.getContentElement());
        this.form.set(kouhi);
        let cmap = this.setCommands(commandsTmpl);
        cmap.enter.addEventListener("click", async event => await this.doEnter());
        cmap.close.addEventListener("click", event => this.close());
    }

    onUpdated(cb){
        this.ele.addEventListener("updated", event => cb(event.detail));
    }

    async doEnter(){
        let kouhiId = this.kouhi.kouhiId;
        let patientId = this.kouhi.patientId;
        if( !(kouhiId > 0) ){
            alert("Invalid kouhiId");
            return;
        }
        if( !(patientId > 0) ){
            alert("Invalid patientId");
            return;
        }
        let data = this.form.get(kouhiId, patientId);
        if( !data ){
            let err = this.form.getError();
            alert(err);
            return;
        }
        await this.rest.updateKouhi(data);
        let updated = await this.rest.getKouhi(kouhiId);
        this.ele.dispatchEvent(new CustomEvent("updated", { detail: updated }));
    }
}