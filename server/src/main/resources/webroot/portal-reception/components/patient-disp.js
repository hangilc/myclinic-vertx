import {DispTable} from "./disp-table.js";
import * as kanjidate from "../js/kanjidate.js";

export class PatientDisp extends DispTable {
    constructor(patient){
        super();
        this.add("患者番号", patient.patientId)
        this.add("氏名", `${patient.lastName} ${patient.firstName}`);
        this.add("よみ", `${patient.lastNameYomi} ${patient.firstNameYomi}`);
        this.add("生年月日", birthdayRep(patient.birthday));
        this.add("性別", sexRep(patient.sex));
        this.add("住所", patient.address);
        this.add("電話", patient.phone);
    }
}

function birthdayRep(birthday){
    let date = kanjidate.sqldateToKanji(birthday);
    let age = kanjidate.calcAge(birthday);
    return `${date}生 ${age}才`;
}

function sexRep(sex){
    switch(sex){
        case "M": return "男性";
        case "F": return "女性";
        default: return "不明";
    }
}
