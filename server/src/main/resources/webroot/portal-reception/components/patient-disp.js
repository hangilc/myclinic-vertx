import {DispTable} from "./disp-table.js";
import * as kanjidate from "../js/kanjidate.js";

export class PatientDisp extends DispTable {
    constructor(patient){
        super();
        this.patientId = this.addField("患者番号", patientIdRep);
        this.name = this.addField("氏名", nameRep);
        this.yomi = this.addField("よみ", yomiRep);
        this.birthday = this.addField("生年月日", birthdayRep);
        this.sex = this.addField("性別", sexRep);
        this.address = this.addField("住所", patient => patient ? patient.address : "");
        this.phone = this.addField("電話", patient => patient ? patient.phone : "");
        if( patient ){
            this.set(patient);
        }
    }

}

function patientIdRep(patient){
    return patient ? "" + patient.patientId : "";
}

function nameRep(patient){
    if( patient ){
        return `${patient.lastName} ${patient.firstName}`;
    } else {
        return "";
    }
}

function yomiRep(patient){
    if( patient ){
        return `${patient.lastNameYomi} ${patient.firstNameYomi}`;
    } else {
        return "";
    }
}

function birthdayRep(patient){
    if( patient ){
        let birthday = patient.birthday;
        let date = kanjidate.sqldateToKanji(birthday);
        let age = kanjidate.calcAge(birthday);
        return `${date}生 ${age}才`;
    } else {
        return "";
    }
}

function sexRep(patient){
    if( patient ){
        let sex = patient.sex;
        switch(sex){
            case "M": return "男性";
            case "F": return "女性";
            default: return "不明";
        }
    } else {
        return "";
    }
}
