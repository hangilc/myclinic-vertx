import {Widget} from "../components/widget.js";
import {BasicInfo} from "./basic-info/basic-info.js";
import {parseElement} from "../js/parse-node.js";

let tmpl = `
<div class="x-basic"></div>
`;

export class PatientWidget extends Widget {
    constructor(patient, rest){
        super(`${patient.lastName}${patient.firstName}（${patient.patientId}）`);
        this.patient = patient;
        this.rest = rest;
        this.getContent().innerHTML = tmpl;
        this.map = parseElement(this.getContent());
        this.addBasic();
    }

    addBasic(){
        let basic = new BasicInfo(this.patient, this.rest);
        basic.ele.addEventListener("patient-updated", event => {
            this.patient = event.detail;
            this.addBasic();
        });
        this.map.basic.innerHTML = "";
        this.map.basic.appendChild(basic.ele);
    }
}