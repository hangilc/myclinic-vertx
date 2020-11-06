import {Widget} from "./widget2.js";
import {DateInput} from "./date-input.js";
import {ShahokokuhoForm} from "./shahokokuho-form.js";
import {RadioInput} from "./radio-input.js";

let commandsHtml = `
    <button type="button" class="x-enter btn btn-secondary">入力</button>
    <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
`;

export class ShahokokuhoNewWidget extends Widget {
    constructor(patientId, rest){
        super();
        this.patientId = patientId;
        this.rest = rest;
        this.setTitle("新規社保国保入力");
        this.form = new ShahokokuhoForm(this.getContentElement());
        let cmap = this.setCommands(commandsHtml);
        cmap.enter.addEventListener("click", async event => await this.doEnter());
        cmap.close.addEventListener("click", event => this.close());
    }

    onEntered(cb){
        this.ele.addEventListener("entered", event => cb(event.detail));
    }

    async doEnter(){
        if( !(this.patientId > 0) ){
            alert("患者が指定されていません。");
            return;
        }
        let data = this.form.get(0, this.patientId);
        if( !data ){
            let err = this.form.getError();
            alert(err);
            return;
        }
        let shahokokuhoId = await this.rest.enterShahokokuho(data);
        let entered = await this.rest.getShahokokuho(shahokokuhoId);
        this.ele.dispatchEvent(new CustomEvent("entered", { detail: entered }));
    }
}