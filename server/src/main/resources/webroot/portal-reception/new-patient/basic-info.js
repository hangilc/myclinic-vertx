import {Widget} from "../components/widget.js";
import {PatientDisp} from "../components/patient-disp.js";

export class BasicInfo extends Widget {
    constructor(patient) {
        super("基本情報");
        let disp = new PatientDisp(patient);
        this.getContent().appendChild(disp.ele);
    }
}