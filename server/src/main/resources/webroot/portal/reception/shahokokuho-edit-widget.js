import {Widget} from "./widget2.js";
import {ShahokokuhoForm} from "./shahokokuho-form.js";

let commandsTmpl = `
    <button type="button" class="x-enter btn btn-secondary">入力</button>
    <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
`;

export class ShahokokuhoEditWidget extends Widget {
    constructor(shahokokuho, rest) {
        super();
        this.setTitle("社保国保編集");
        this.shahokokuho = shahokokuho;
        this.rest = rest;
        let form = new ShahokokuhoForm(this.getContentElement());
        form.set(shahokokuho);
        this.form = form;
        let cmap = this.setCommands(commandsTmpl);
        cmap.enter.addEventListener("click", async event => await this.doEnter());
        cmap.close.addEventListener("click", event => this.close());
    }

    onUpdated(cb) {
        this.ele.addEventListener("updated", event => cb(event.detail));
    }

    async doEnter() {
        let data = this.form.get(this.shahokokuho.shahokokuhoId, this.shahokokuho.patientId);
        if (!data) {
            let err = this.form.getError();
            alert(err);
            return;
        }
        await this.rest.updateShahokokuho(data);
        let updated = await this.rest.getShahokokuho(this.shahokokuho.shahokokuhoId);
        this.ele.dispatchEvent(new CustomEvent("updated", {detail: updated}));
    }
}