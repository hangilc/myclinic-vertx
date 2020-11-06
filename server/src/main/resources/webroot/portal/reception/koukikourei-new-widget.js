import {Widget} from "./widget2.js";
import {KoukikoureiForm} from "./koukikourei-form.js";

let commandsTmpl = `
    <button type="button" class="x-enter btn btn-secondary">入力</button>
    <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
`;

export class KoukikoureiNewWidget extends Widget {
    constructor(patientId, rest) {
        super();
        this.setTitle("新規後期高齢入力");
        this.patientId = patientId;
        this.rest = rest;
        this.form = new KoukikoureiForm(this.getContentElement());
        let cmap = this.setCommands(commandsTmpl);
        cmap.enter.addEventListener("click", async event => await this.doEnter());
        cmap.close.addEventListener("click", event => this.close());
    }

    onEntered(cb) {
        this.ele.addEventListener("entered", event => cb(event.detail));
    }

    async doEnter() {
        let patientId = this.patientId;
        if (!(patientId > 0)) {
            alert("患者が設定されていません。");
            return null;
        }
        let data = this.form.get(0, patientId);
        if (!data) {
            let err = this.form.getError();
            if (err) {
                alert(err);
            }
            return;
        }
        let koukikoureiId = await this.rest.enterKoukikourei(data);
        let entered = await this.rest.getKoukikourei(koukikoureiId);
        this.ele.dispatchEvent(new CustomEvent("entered", {detail: entered}));
    }

}