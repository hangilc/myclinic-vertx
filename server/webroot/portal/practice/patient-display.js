import * as kanjidate from "../js/kanjidate.js";

export class PatientDisplay {
    constructor(map){
        this.patientId = map.patientId;
        this.patientName = map.patientName;
        this.patientYomi = map.patientYomi;
        this.patientBirthday = map.patientBirthday;
        this.patientSex = map.patientSex;
        this.patientAddress = map.patientAddress;
        this.patientPhone = map.patientPhone;
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

