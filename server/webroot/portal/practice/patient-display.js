import * as kanjidate from "../js/kanjidate.js";

export class PatientDisplay {
    constructor(map){
        this.patientId = map.patientId.ele;
        this.patientName = map.patientName.ele;
        this.patientYomi = map.patientYomi.ele;
        this.patientBirthday = map.patientBirthday.ele;
        this.patientSex = map.patientSex.ele;
        this.patientAddress = map.patientAddress.ele;
        this.patientPhone = map.patientPhone.ele;
    }

    setPatient(patient){
        this.patientId.text(patient.patientId);
        this.patientName.text(`${patient.lastName} ${patient.firstName}`);
        this.patientYomi.text(`${patient.lastNameYomi} ${patient.firstNameYomi}`);
        this.patientBirthday.text(birthdayRep(patient.birthday));
        this.patientSex.text(sexRep(patient.sex));
        this.patientAddress.text(patient.address);
        this.patientPhone.text(patient.phone);
    }
}

function birthdayRep(birthday){
    let rep = kanjidate.sqldateToKanji(birthday);
    let age = moment().diff(moment(birthday), "years");
    return `${rep}生　${age}才`;
}

function sexRep(sex){
    if( sex === "M" ){
        return "男";
    } else {
        return "女";
    }
}

