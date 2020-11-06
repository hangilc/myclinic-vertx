export class PatientDisp {
    constructor(map) {
        this.map = map;
    }

    set(patient){
        if( patient ){
            this.map.patientId.text(patient.patientId);
            this.map.lastName.text(patient.lastName);
            this.map.firstName.text(patient.firstName);
            this.map.lastNameYomi.text(patient.lastNameYomi);
            this.map.firstNameYomi.text(patient.firstNameYomi);
            this.map.birthday.text(patient.birthday);
            this.map.sex.text(patient.sex);
            this.map.address.text(patient.address);
            this.map.phone.text(patient.phone);
        } else {
            this.map.patientId.text("");
            this.map.lastName.text("");
            this.map.firstName.text("");
            this.map.lastNameYomi.text("");
            this.map.firstNameYomi.text("");
            this.map.birthday.text("");
            this.map.sex.text("");
            this.map.address.text("");
            this.map.phone.text("");
        }
    }
}