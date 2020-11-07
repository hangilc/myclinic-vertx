import {parseElement} from "../js/parse-node.js";
import {PatientForm} from "../components/patient-form.js";

let tmpl = `
<h3>新規患者</h3>
<form class="x-form"></form>
`;

export class NewPatientPanel {
    constructor(ele){
        ele.innerHTML = tmpl;
        let map = parseElement(ele);
        let form = new PatientForm(map.form);
    }
}