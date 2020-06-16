export class PatientForm {
    constructor(map) {
        this.map = map;
        this.patientIdElement = map.patientId;
        this.lastNameElement = map.lastName;
        this.firstNameElement = map.firstName;
        this.lastNameYomiElement = map.lastNameYomi;
        this.firstNameYomiElement = map.firstNameYomi;
        this.birthdayElement = map.birthday;
        this.sexElement = map.sex;
        this.addressElement = map.address;
        this.phoneElement = map.phone;
    }

    init(){

    }

    set(patient){
        if( patient ){
            if( this.patientIdElement ){
                this.patientIdElement.text(patient.patientId);
            }
            this.lastNameElement.val(patient.lastName);
            this.firstNameElement.val(patient.firstName);
            this.lastNameYomiElement.val(patient.lastNameYomi);
            this.firstNameYomiElement.val(patient.firstNameYomi);
            this.birthdayElement.val(patient.birthday);
            this.sexElement.val(patient.sex);
            this.addressElement.val(patient.address);
            this.phoneElement.val(patient.phone);
        } else {
            if( this.patientIdElement ){
                this.patientIdElement.text(null);
            }
            this.lastNameElement.val(null);
            this.firstNameElement.val(null);
            this.lastNameYomiElement.val(null);
            this.firstNameYomiElement.val(null);
            this.birthdayElement.val(null);
            this.sexElement.val(null);
            this.addressElement.val(null);
            this.phoneElement.val(null);
        }
    }

}