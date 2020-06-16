import {Widget} from "./widget.js";
import {PatientForm} from "./patient-form.js";
import {DateInput} from "./date-input.js";
import {SexInput} from "./sex-input.js";

export class PatientEditWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        let formMap = Object.assign({}, map.form, {
            birthday: new DateInput(map.form.birthday),
            sex: new SexInput(map.form.sex, "sex")
        });
        this.form = new PatientForm(formMap);
    }

    init(){
        super.init();
        return this;
    }

    set(patient){
        super.set();
        this.form.set(patient);
        return this;
    }
}