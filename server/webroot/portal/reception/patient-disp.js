import {Component} from "../js/component.js";
import * as kanjidate from "../js/kanjidate.js";
import * as SexUtil from "../js/sex-util.js";

export class PatientDisp extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.patientIdElement = map.patientId;
        this.lastNameElement = map.lastName;
        this.firstNameElement = map.firstName;
        this.lastNameYomiElement = map.lastNameYomi;
        this.firstNameYomiElement = map.firstNameYomi;
        this.birthdayElement = map.birthday;
        this.sexElement = map.sex;
        this.addressElement = map.address;
        this.phoneElement = map.phone;
        this.ageElement = map.age;
    }

    init(){
        return this;
    }

    set(patient){
        if( patient ) {
            this.setField(this.patientIdElement, patient.patientId, this.patientIdConv);
            this.setField(this.lastNameElement, patient.lastName, this.lastNameConv);
            this.setField(this.firstNameElement, patient.firstName, this.firstNameConv);
            this.setField(this.lastNameYomiElement, patient.lastNameYomi, this.lastNameYomiConv);
            this.setField(this.firstNameYomiElement, patient.firstNameYomi, this.firstNameYomiConv);
            this.setField(this.birthdayElement, patient.birthday, this.birthdayConv);
            this.setField(this.sexElement, patient.sex, this.sexConv);
            this.setField(this.addressElement, patient.address, this.addressConv);
            this.setField(this.phoneElement, patient.phone, this.phoneConv);
            this.setField(this.ageElement, patient.birthday, this.ageConv);
        } else {
            this.setField(this.patientIdElement, null, null);
            this.setField(this.lastNameElement, null, null);
            this.setField(this.firstNameElement, null, null);
            this.setField(this.lastNameYomiElement, null, null);
            this.setField(this.firstNameYomiElement, null, null);
            this.setField(this.birthdayElement, null, null);
            this.setField(this.sexElement, null, null);
            this.setField(this.addressElement, null, null);
            this.setField(this.phoneElement, null, null);
            this.setField(this.ageElement, null, null);
        }
        return this;
    }

    setField(ele, value, conv){
        if( ele ){
            if( conv ){
                value = conv(value);
            }
            ele.text(value == null ? "" : value.toString());
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
