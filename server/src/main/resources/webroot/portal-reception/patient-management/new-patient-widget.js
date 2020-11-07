import {Widget} from "../components/widget.js";
import {PatientForm} from "../components/patient-form.js";
import {ComEnterCancel} from "../components/com-enter-cancel.js";

export class NewPatientWidget extends Widget {
    constructor(rest){
        super("新規患者入力");
        this.rest = rest;
        this.form = new PatientForm();
        this.getContent().appendChild(this.form.ele);
        let cmap = ComEnterCancel.populate(this.getCommands());
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