import {parseElement} from "../js/parse-node.js";
import {PatientForm} from "../components/patient-form.js";

let tmpl = `
<div class="x-form mb-2"></div>
<div class="text-right">
    <button class="btn btn-primary x-enter">入力</button>
    <button class="btn btn-secondary x-cancel">キャンセル</button>
</div>
`;

export class EnterPatient {
    constructor(ele, rest){
        if( !ele ){
            ele = document.createElement("div");
        }
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

    onEntered(cb){
        this.ele.addEventListener("entered", event => cb(event.detail));
    }

    async doEnter(){
        let patientOpt = this.form.get();
        if( !patientOpt.ok ){
            alert(patientOpt.message);
        } else {
            let patient = patientOpt.value;
            console.log(patient);
            patient.patientId = await this.rest.enterPatient(patient);
            this.ele.dispatchEvent(new CustomEvent("entered", { detail: patient }));
        }
    }

    onCancelled(cb){
        this.ele.addEventListener("cancelled", event => cb());
    }

    doCancel(){
        this.ele.dispatchEvent(new Event("cancelled"));
    }
}