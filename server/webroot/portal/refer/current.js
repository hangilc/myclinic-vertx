import {Component} from "./component.js";

export class Current extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.patientIdElement = map.patientId;
        this.nameElement = map.name;
    }

    init(){
        super.init();
        return this;
    }

    set(patient){
        super.set();
        this.patientIdElement.text(patient.patientId);
        this.nameElement.text(patient.lastName + patient.firstName);
        return this;
    }
}
