import {parseElement} from "../js/parse-node.js";
import {PatientDisp} from "../components/patient-disp.js";

let tmpl = `
<div class="x-basic-info"></div>
`;

export class EnterHoken {
    constructor(ele, rest, patient){
        if( !ele ){
            ele = document.createElement("div");
        }
        this.ele = ele;
        this.rest = rest;
        ele.innerHTML = tmpl;
        let map = parseElement(ele);
        let patientDisp = new PatientDisp(patient);
        map.basicInfo.appendChild(patientDisp.ele);
    }
}