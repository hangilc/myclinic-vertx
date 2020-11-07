import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {PatientDisp} from "../../components/patient-disp.js";
import {PatientForm} from "../../components/patient-form.js";

let tmpl = `
<div class="row">
    <div class="col-9 x-main"></div>
    <div class="col-3 x-right"></div>
</div>
`;

let dispCommandsTmpl = `
<button class="btn btn-link x-edit">編集</button>
`;

let editCommandsTmpl = `
<div class="mb-2"><button class="btn btn-primary x-enter">入力</button></div>
<div><button class="btn btn-secondary x-cancel">キャンセル</button></div>
`;

export class BasicInfo {
    constructor(patient, rest){
        this.patient = patient;
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.showDisp();
    }

    showDisp(){
        let disp = new PatientDisp(this.patient);
        this.map.main.innerHTML = "";
        this.map.main.appendChild(disp.ele);
        this.map.right.innerHTML = dispCommandsTmpl;
        let cmap = parseElement(this.map.right);
        cmap.edit.addEventListener("click", event => this.doEdit());
    }

    doEdit(){
        let form = new PatientForm(this.patient);
        this.map.main.innerHTML = "";
        this.map.main.append(form.ele);
        this.map.right.innerHTML = editCommandsTmpl;
        let cmap = parseElement(this.map.right);
        cmap.enter.addEventListener("click", async event => {
            let updatedOpt = form.get();
            if( updatedOpt.ok ){
                let updated = updatedOpt.value;
                await this.rest.updatePatient(updated);
                this.ele.dispatchEvent(new CustomEvent("patient-updated", { detail: updated }));
            } else {
                alert(updatedOpt.message);
            }
        });
        cmap.cancel.addEventListener("click", event => this.showDisp());
    }
}