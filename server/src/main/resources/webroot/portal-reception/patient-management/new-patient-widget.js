import {Widget} from "../components/widget.js";
import {PatientForm} from "../components/patient-form.js";
import {parseElement} from "../js/parse-node.js";

let commandsTmpl = `
    <button class="x-enter btn btn-primary">入力</button>
    <button class="x-cancel btn btn-sm btn-secondary">キャンセル</button>
`;

export class NewPatientWidget extends Widget {
    constructor(rest){
        super("新規患者入力");
        this.rest = rest;
        this.form = new PatientForm();
        this.getContent().appendChild(this.form.ele);
        this.getCommands().innerHTML = commandsTmpl;
        let cmap = parseElement(this.getCommands());
        cmap.enter.addEventListener("click", async event => await this.doEnter());
        cmap.cancel.addEventListener("click", event => this.close());
    }

    async doEnter(){
        let patientOpt = this.form.get();
        if( !patientOpt.ok ){
            alert(patientOpt.message);
        } else {
            let patient = patientOpt.value;
            patient.patientId = await this.rest.enterPatient(patient);
            this.ele.dispatchEvent(new CustomEvent("patient-entered", { detail: patient }));
        }
    }
}