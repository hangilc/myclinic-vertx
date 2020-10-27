import {Widget} from "./widget2.js";
import {DateInput} from "./date-input.js";
import {KouhiForm} from "./kouhi-form.js";

let commandsTmpl = `
    <button type="button" class="x-enter btn btn-secondary">入力</button>
    <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
`;

export class KouhiNewWidget extends Widget {
    constructor(patientId, rest){
        super();
        this.patientId = patientId;
        this.rest = rest;
        this.setTitle("新規公費負担入力");
        this.form = new KouhiForm(this.getContentElement());
        let cmap = this.setCommands(commandsTmpl);
        cmap.enter.addEventListener("click", async event => await this.doEnter());
        cmap.close.addEventListener("click", event => this.close());
    }

    onEntered(cb){
        this.ele.addEventListener("entered", event => cb(event.detail));
    }

    async doEnter(){
        let patientId = this.patientId;
        if( !(patientId > 0) ){
            this.error = "患者が設定されていません。";
            return undefined;
        }
        let data = this.form.get(0, patientId);
        if( !data ){
            let err = this.form.getError();
            alert(err);
            return;
        }
        let kouhiId = await this.rest.enterKouhi(data);
        let entered = await this.rest.getKouhi(kouhiId);
        this.ele.dispatchEvent(new CustomEvent("entered", { detail: entered }));
    }

}
