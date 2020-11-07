import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {NewPatientWidget} from "./new-patient-widget.js";
import {PatientWidget} from "./patient-widget.js";

let tmpl = `
<div>
    <h3 class="mb-3">患者管理</h3>
    <div class="mb-2">
        <button class="btn btn-primary x-new-patient">新規患者</button>
    </div>
    <div class="x-workarea pt-2"></div>
</div>
`;

export class PatientManagementPanel {
    constructor(rest){
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.newPatient.addEventListener("click", event => this.doNewPatient());
        (async function(self){
            let patient = await rest.getPatient(3);
            let w = new PatientWidget(patient, rest);
            self.map.workarea.append(w.ele);
        })(this);
    }

    doNewPatient(){
        let w = new NewPatientWidget(this.rest);
        w.ele.addEventListener("patient-entered", event => {
            let patient = event.detail;
            console.log(patient);
            w.close();
        });
        this.map.workarea.prepend(w.ele);
    }
}