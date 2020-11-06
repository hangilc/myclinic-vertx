import * as kanjidate from "../js/kanjidate.js";
import * as SexUtil from "../js/sex-util.js";

export class PatientDisp {

    constructor(map){
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

    set(patient){
        if( patient ) {
            this.setPatientId(patient.patientId);
            this.setLastName(patient.lastName);
            this.setFirstName(patient.firstName);
            this.setLastNameYomi(patient.lastNameYomi);
            this.setFirstNameYomi(patient.firstNameYomi);
            this.setBirthday(patient.birthday);
            this.setSex(patient.sex);
            this.setAddress(patient.address);
            this.setPhone(patient.phone);
        } else {
            this.clearPatientId();
            this.clearLastName();
            this.clearFirstName();
            this.clearLastNameYomi();
            this.clearFirstNameYomi();
            this.clearBirthday();
            this.clearSex();
            this.clearAddress();
            this.clearPhone();
        }
    }

    setPatientId(patientId){
        if( this.patientIdElement ){
            this.patientIdElement.text("" + patientId);
        }
    }

    clearPatientId(){
        if( this.patientIdElement ){
            this.patientIdElement.text("");
        }
    }

    setLastName(lastName){
        if( this.lastNameElement ){
            this.lastNameElement.text(lastName);
        }
    }

    clearLastName(){
        if( this.lastNameElement ){
            this.lastNameElement.text("");
        }
    }

    setFirstName(firstName){
        if( this.firstNameElement ){
            this.firstNameElement.text(firstName);
        }
    }

    clearFirstName(){
        if( this.firstNameElement ){
            this.firstNameElement.text("");
        }
    }

    setLastNameYomi(lastNameYomi){
        if( this.lastNameYomiElement ){
            this.lastNameYomiElement.text(lastNameYomi);
        }
    }

    clearLastNameYomi(){
        if( this.lastNameYomiElement ){
            this.lastNameYomiElement.text("");
        }
    }

    setFirstNameYomi(firstNameYomi){
        if( this.firstNameYomiElement ){
            this.firstNameYomiElement.text(firstNameYomi);
        }
    }

    clearFirstNameYomi(){
        if( this.firstNameYomiElement ){
            this.firstNameYomiElement.text("");
        }
    }

    setBirthday(birthday){
        if( this.birthdayElement ){
            let format = this.birthdayElement.data("format");
            if( format === "kanji" ){
                birthday = kanjidate.sqldateToKanji(birthday);
            }
            this.birthdayElement.text(birthday);
        }
    }

    clearBirthday(){
        if( this.birthdayElement ){
            this.birthdayElement.text("");
        }
    }

    setSex(sex){
        if( this.sexElement ){
            let format = this.sexElement.data("format");
            if( format === "kanji" ){
                sex = SexUtil.sexAsKanji(sex);
            }
            this.sexElement.text(sex);
        }
    }

    clearSex(){
        if( this.sexElement ){
            this.sexElement.text("");
        }
    }

    setAddress(address){
        if( this.addressElement ){
            this.addressElement.text(address);
        }
    }

    clearAddress(){
        if( this.addressElement ){
            this.addressElement.text("");
        }
    }

   setPhone(phone){
        if( this.phoneElement ){
            this.phoneElement.text(phone);
        }
    }

    clearPhone(){
        if( this.phoneElement ){
            this.phoneElement.text("");
        }
    }




    setBirthdayConv(conv){
        this.birthdayConv = conv;
    }

    setSexConv(conv){
        this.sexConv = conv;
    }

    birthdayAsKanji(birthday, opt){
        if( !opt ){
            opt = {};
        }
        let data = kanjidate.sqldatetimeToData(birthday);
        let gengou = data.gengou.name;
        let nen = data.nen + "";
        let month = data.month + "";
        let day = data.day + "";
        if( opt.padZero ){
            nen = nen.padStart(2, "0");
            month = month.padStart(2, "0");
            day = day.padStart(2, "0");
        }
        let suffix = "";
        if( opt.suffix ){
            suffix = opt.suffix;
        }
        return `${gengou}${nen}年${month}月${day}日${suffix}`;
    }

    calcAge(birthday){
        return kanjidate.calcAge(birthday);
    }

    sexAsKanji(sex){
        return SexUtil.sexAsKanji(sex);
    }

}
