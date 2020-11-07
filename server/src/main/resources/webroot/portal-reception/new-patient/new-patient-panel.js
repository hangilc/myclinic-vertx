import {parseElement} from "../js/parse-node.js";
import {EnterPatient} from "./enter-patient.js";
import {EnterHoken} from "./enter-hoken.js";

let tmpl = `
<h3>新規患者</h3>
<div class="x-workarea"></div>
`;

export class NewPatientPanel {
    constructor(ele, rest){
        ele.innerHTML = tmpl;
        this.ele = ele;
        this.rest = rest;
        let map = parseElement(ele);
        (async function(){
            let patient = await rest.getPatient(2);
            let enterHoken = new EnterHoken(map.workarea, rest, patient);
        })();
        // let enterPatient = new EnterPatient(map.workarea);
        // enterPatient.onEntered(patient => {});
        // enterPatient.onCancelled(() => new NewPatientPanel(ele, rest));
    }

}