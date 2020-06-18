import * as kanjidate from "../js/kanjidate.js";

let identity = (x) => x;

export class PatientDisplay {
    constructor(map, opts){
        opts = opts || {};
        this.opts = Object.assign({}, opts);
        this.opts.formatter = Object.assign({
            patientId: value => "" + value,
            lastName: identity,
            firstName: identity,
            lastNameYomi: identity,
            firstNameYomi: identity,
            birthday: data => data.kanji,
            sex: data => data.kanji,
            address: identity,
            phone: identity,
            age: age => "" + age
        }, opts.formatter);
        this.patientId = map.patientId;
        this.patientLastName = map.lastName;
        this.patientFirstName = map.firstName;
        this.patientLastNameYomi = map.lastNameYomi;
        this.patientFirstNameYomi = map.firstNameYomi;
        this.patientBirthday = map.birthday;
        this.patientSex = map.sex;
        this.patientAddress = map.address;
        this.patientPhone = map.phone;
        this.patientAge = map.age;
    }

    set(ele, value, formatter){
        if( ele ){
            if( formatter ){
                value = formatter(value);
            }
            ele.text(value);
        }
    }

    clearField(ele){
        if( ele ){
            ele.text("");
        }
    }

    clear(){
        this.clearField(this.patientId);
        this.clearField(this.patientLastName);
        this.clearField(this.patientFirstName);
        this.clearField(this.patientLastNameYomi);
        this.clearField(this.patientFirstNameYomi);
        this.clearField(this.patientBirthday);
        this.clearField(this.patientSex);
        this.clearField(this.patientAddress);
        this.clearField(this.patientPhone);
        this.clearField(this.patientAge);
    }

    setPatient(patient){
        if( patient ){
            let formatter = this.opts.formatter;
            this.set(this.patientId, patient.patientId, formatter.patientId);
            this.set(this.patientLastName, patient.lastName, formatter.lastName);
            this.set(this.patientFirstName, patient.firstName, formatter.firstName);
            this.set(this.patientLastNameYomi, patient.lastNameYomi, formatter.lastNameYomi);
            this.set(this.patientFirstNameYomi, patient.firstNameYomi, formatter.firstNameYomi);
            this.set(this.patientBirthday, birthdayData(patient.birthday), formatter.birthday);
            this.set(this.patientSex, sexData(patient.sex), formatter.sex);
            this.set(this.patientAddress, patient.address, formatter.address);
            this.set(this.patientPhone, patient.phone, formatter.phone);
            this.set(this.patientAge, ageData(patient.birthday), formatter.age);
        } else {
            this.clear();
        }
    }
}

function birthdayData(birthday){
    return kanjidate.sqldateToData(birthday);
}

let sexDataMap = {
    "M": {alpha: "Male", kanji: "男"},
    "F": {alpha: "Female", kanji: "女"}
};

function sexData(sex){
    return sexDataMap[sex];
}

function ageData(birthday){
    return kanjidate.calcAge(birthday);
}

