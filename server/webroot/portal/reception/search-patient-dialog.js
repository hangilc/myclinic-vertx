import {Component} from "../js/component.js";
import {SearchPatient} from "./search-patient.js";
import {PatientDisp} from "./patient-disp.js";

export class Body extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.search = new SearchPatient(ele, map, rest);
    }

    init(){
        this.search.init();
        this.search.onSelected(patient => this.trigger("selected", patient));
        return this;
    }

    setDialog(dialog){
        this.dialog = dialog;
    }

    set(){
        this.search.set();
        return this;
    }

    focus(){
        this.search.focus();
    }

    onSelected(cb){
        this.on("selected", (event, patient) => cb(patient));
    }
}

export class Footer extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.disp = new PatientDisp(map.disp_, map.disp, rest);
    }

    init(){
        this.disp.init();
        this.disp.setBirthdayConv(birthday => this.disp.birthdayAsKanji(birthday, {
            suffix: "生"
        }) + " " + this.disp.calcAge(birthday) + "才");
        this.disp.setSexConv(sex => this.disp.sexAsKanji(sex));
        return this;
    }

    setDialog(dialog){
        this.dialog = dialog;
    }

    set(patient){
        this.patient = patient;
        this.disp.set(patient);
        return this;
    }
}
