import {parseElement} from "../js/parse-node.js";
import {PatientForm} from "../components/patient-form.js";

let tmpl = `
<h3>新規患者</h3>
<div class="x-form mb-2"></div>
<div class="text-right">
    <button class="btn btn-primary x-enter">入力</button>
    <button class="btn btn-secondary x-cancel">キャンセル</button>
</div>
`;

export class NewPatientPanel {
    constructor(ele, rest){
        ele.innerHTML = tmpl;
        this.ele = ele;
        this.rest = rest;
        let map = parseElement(ele);
        let form = new PatientForm();
        this.form = form;
        map.form.appendChild(form.ele);
        map.enter.addEventListener("click", async event => await this.doEnter());
        map.cancel.addEventListener("click", event => this.doCancel());
    }

    async doEnter(){
        let patientOpt = this.form.get();
        if( !patientOpt.ok ){
            alert(patientOpt.message);
        } else {
            let patient = patientOpt.value;
            console.log(patient);
            let patientId = await this.rest.enterPatient(patient);
            console.log(patientId);
        }
    }

    doCancel(){
        this.ele.innerHTML = "";
        new NewPatientPanel(this.ele, this.rest);
    }
}