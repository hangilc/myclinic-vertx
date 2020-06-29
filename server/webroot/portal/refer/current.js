import {Component} from "./component.js";
import * as kanjidate from "../js/kanjidate.js";
import {Prev} from "./prev.js";

export class Current extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.prev = new Prev(map.prev_, map.prev, rest);
        this.patientIdElement = map.patientId;
        this.nameElement = map.name;
        this.printElement = map.print;
        this.saveElement = map.save;
        this.referHospitalElement = map.referHospital;
        this.referDoctorElement = map.referDoctor;
        this.patientNameElement = map.patientName;
        this.patientInfoElement = map.patientInfo;
        this.diagnosisElement = map.diagnosis;
        this.contentElement = map.content;
        this.issueDateElement = map.issueDate;
    }

    init(){
        super.init();
        this.prev.init();
        this.printElement.on("click", event => this.doPrint());
        this.saveElement.on("click", event => this.doSave());
        return this;
    }

    set(patient, prevs){
        super.set();
        this.prev.set(prevs);
        this.patient = patient;
        this.patientIdElement.text(patient.patientId);
        this.nameElement.text(patient.lastName + patient.firstName);
        return this;
    }

    async compileData(){
        let data = { };
        if( this.patient ){
            let patient = this.patient;
            data.patientName = `患者： ${patient.lastName}${patient.firstName} 様`;
            let birthday = kanjidate.sqldateToKanji(patient.birthday);
            let age = kanjidate.calcAge(patient.birthday);
            let sex = patient.sex === "M" ? "男" : "女";
            data.patientInfo = `${birthday}生 ${age}才 ${sex}性`;
        }
        let clinicInfo = await this.rest.getClinicInfo();
        data.referHospital = this.referHospitalElement.val();
        data.referDoctor = this.referDoctorElement.val();
        data.diagnosis = "診断： " + this.diagnosisElement.val();
        data.content = this.contentElement.val();
        data.issueDate = this.issueDateElement.val();
        data.clinicPostalCode = clinicInfo.postalCode;
        data.clinicAddress = clinicInfo.address;
        data.clinicPhone = "電話 " + clinicInfo.tel;
        data.clinicFax = "FAX " + clinicInfo.fax;
        data.clinicName = clinicInfo.name;
        data.doctorName = clinicInfo.doctorName;
        return data;
    }

    async doPrint(){
        let data = await this.compileData();
        let ops = await this.rest.referDrawer(data);
        await this.rest.printDrawer([ops]);
    }

    async doSave(){
        let patient = this.patient;
        if( patient ){
            let data = await this.compileData();
            let file = await this.rest.saveRefer(data, patient.patientId);
            console.log(file);
        }
    }
}
