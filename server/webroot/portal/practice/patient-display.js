import * as kanjidate from "../js/kanjidate.js";

export class PatientDisplay {
    constructor(props){
        this.patientId = props["patient-id"];
        this.patientName = props["patient-name"];
        this.patientYomi = props["patient-yomi"];
        this.patientBirthday = props["patient-birthday"];
        this.patientSex = props["patient-sex"];
        this.patientAddress = props["patient-address"];
        this.patientPhone = props["patient-phone"];
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

