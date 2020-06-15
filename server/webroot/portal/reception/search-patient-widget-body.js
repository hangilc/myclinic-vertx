import {Component} from "../js/component.js";
import {SearchPatient} from "./search-patient.js";
import {PatientDisp} from "./patient-disp.js";

export class SearchPatientWidgetBody extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.search = new SearchPatient(map.search_, map.search, rest);
        this.disp = new PatientDisp(map.disp_, map.disp, rest);
        this.editElement = map.edit;
        this.registerElement = map.register;
    }

    init(){
        this.search.init();
        this.search.onSelected(patient => this.doSelected(patient));
        this.disp.init();
        this.setupDispConverters(this.disp);
        this.editElement.on("click", event => this.doEdit());
        this.registerElement.on("click", event => this.doRegister());
        return this;
    }

    set(){
        this.search.set();
        this.disp.set();
        return this;
    }

    focus(){
        this.search.focus();
    }

    onEdit(cb){
        this.on("edit", (event, patient) => cb(patient));
    }

    doEdit(){
        let patient = this.patient;
        if( patient ){
            this.trigger("edit", patient);
        }
    }

    onRegistered(cb){
        this.on("registered", (event, patient) => cb(patient));
    }

    doRegister(){
        let patient = this.patient;
        if( patient ){
            alert("not implemented");
        }
    }

    doSelected(patient){
        this.patient = patient;
        if( patient ){
            this.disp.set(patient);
        }
    }

    setupDispConverters(disp){
        disp.setBirthdayConv(birthday => this.disp.birthdayAsKanji(birthday, {
            suffix: "生"
        }) + " " + this.disp.calcAge(birthday) + "才");
        disp.setSexConv(sex => this.disp.sexAsKanji(sex));
    }
}
