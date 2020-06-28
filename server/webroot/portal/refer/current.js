import {Component} from "./component.js";

export class Current extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.patientIdElement = map.patientId;
        this.nameElement = map.name;
        this.printElement = map.print;
    }

    init(){
        super.init();
        this.printElement.on("click", event => this.doPrint());
        return this;
    }

    set(patient){
        super.set();
        this.patient = patient;
        this.patientIdElement.text(patient.patientId);
        this.nameElement.text(patient.lastName + patient.firstName);
        return this;
    }

    async doPrint(){
        let data = { };
        if( this.patient ){
            let patient = this.patient;
            data.patientName = `患者： ${patient.lastName}${patient.firstName} 様`;
        }
        let ops = await this.rest.referDrawer(data);
        console.log(JSON.stringify(ops, null, 2));
    }
}
