import {parseElement} from "../js/parse-node.js";
import {PatientDisp} from "../components/patient-disp.js";
import {Widget} from "../components/widget.js";

let commandsTmpl = `
    <button class="btn btn-primary">入力</button>
    <button class="btn btn-secondary">キャンセル</button>
`;

export class EnterHoken extends Widget {
    constructor(patient, rest){
        super("基本情報");
        this.rest = rest;
        let patientDisp = new PatientDisp(patient);
        this.getContent().appendChild(patientDisp.ele);
        this.getCommands().innerHTML = commandsTmpl;
    }
}