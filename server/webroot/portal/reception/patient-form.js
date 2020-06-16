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
        this.error = null;
    }

    init(){

    }

    set(patient){
        if( patient ){
            this.patientId = patient.patientId;
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

    getError(){
        let err = this.error;
        this.error = null;
        return err;
    }

    get(){
        let patientId = this.patientId || 0;
        let lastName = this.lastNameElement.val();
        if( lastName === "" ){
            this.error = "姓が入力されていません。";
            return undefined;
        }
        let firstName = this.firstNameElement.val();
        if( firstName === "" ){
            this.error = "名が入力されていません。";
            return undefined;
        }
        let lastNameYomi = this.lastNameYomiElement.val();
        if( lastNameYomi === "" ){
            this.error = "姓のよみが入力されていません。";
            return undefined;
        }
        let firstNameYomi = this.firstNameYomiElement.val();
        if( firstNameYomi === "" ){
            this.error = "名のよみが入力されていません。";
            return undefined;
        }
        let birthday = this.birthdayElement.val();
        if( !birthday ){
            this.error = "生年月日の入力が不適切です。";
            return undefined;
        }
        let sex = this.sexElement.val();
        if( !sex ){
            this.error = "性の入力が不適切です。";
            return undefined;
        }
        let address = this.addressElement.val();
        let phone = this.phoneElement.val();
        return {
            patientId: patientId,
            lastName: lastName,
            firstName: firstName,
            lastNameYomi: lastNameYomi,
            firstNameYomi: firstNameYomi,
            birthday: birthday,
            sex: sex,
            address: address,
            phone: phone
        };
    }

}