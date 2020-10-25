import {Widget} from "./widget2.js";
import {PatientForm} from "./patient-form.js";

export class PatientNewWidget extends Widget {
    constructor() {
        super();
        this.setTitle("新規患者登録");
        let form = new PatientForm(this.getContentElement());
        form.hidePatientId();
    }
}