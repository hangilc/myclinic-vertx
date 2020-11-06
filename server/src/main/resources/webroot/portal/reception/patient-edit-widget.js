import {Widget} from "./widget2.js";
import {PatientForm} from "./patient-form.js";
import {parseElement} from "../js/parse-node.js";

let commandsHtml = `
    <button type="button" class="x-enter btn btn-secondary">入力</button>
    <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
`;

export class PatientEditWidget extends Widget {
    constructor(patient, rest) {
        super();
        this.setTitle("患者情報編集");
        this.rest = rest;
        this.form = new PatientForm();
        this.form.set(patient);
        this.getContentElement().append(this.form.ele);
        this.getCommandsElement().innerHTML = commandsHtml;
        let cmap = parseElement(this.getCommandsElement());
        cmap.close.addEventListener("click", event => this.close());
        cmap.enter.addEventListener("click", event => this.doEnter());
    }

    onUpdated(cb) {  // cb: patient => {}
        this.ele.addEventListener("updated", event => cb(event.detail));
    }

    async doEnter() {
        let data = this.form.get();
        if (data === undefined) {
            let err = this.form.getError();
            if( err ){
                alert(err);
            }
            return;
        }
        if( !data.patientId > 0 ){
            alert("Missing patientId");
            return;
        }
        await this.rest.updatePatient(data);
        this.ele.dispatchEvent(new CustomEvent("updated", { detail: data }));
    }
}