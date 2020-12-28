import * as kanjidate from "../../js/kanjidate.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";

let identity = (x) => x;

let tmpl = `
    <div>
        [<span class="x-patient-id"></span>]
        <span class="x-last-name"></span><span class="x-first-name"></span>
        (<span class="x-last-name-yomi"></span><span class="x-first-name-yomi"></span>)
        <span class="x-birthday"></span>生
        (<span class="x-age"></span>才)
        <span class="x-sex"></span>性
        <a href="javascript:void(0)" class="x-detail-link">詳細</a>
        <div class="x-detail d-none row">
            <div class="col-sm-3">住所</div>
            <div class="x-address col-sm-9"></div>
            <div class="col-sm-3">電話番号</div>
            <div class="x-phone col-sm-9"></div>
        </div>
    </div>
`;

export class PatientDisplay {
    constructor(patient){
        this.ele = createElementFrom(tmpl);
        let map = parseElement(this.ele);
        map.patientId.innerText = patient.patientId;
        map.lastName.innerText = patient.lastName;
        map.firstName.innerText = patient.firstName;
        map.lastNameYomi.innerText = patient.lastNameYomi;
        map.firstNameYomi.innerText = patient.firstNameYomi;
        map.birthday.innerText = kanjidate.sqldateToKanji(patient.birthday);
        map.age.innerText = kanjidate.calcAge(patient.birthday) + "才";
        map.sex.innerText = patient.sex === "M" ? "男性" : "女性";
        map.address.innerText = patient.address;
        map.phone.innerText = patient.phone;
        map.detailLink.addEventListener("click", event => {
            if( map.detail.classList.contains("d-none") ){
                map.detail.classList.remove("d-none");
            } else {
                map.detail.classList.add("d-none");
            }
        });
    }
}

// class PatientDisplayOrig {
//     constructor(map, opts){
//         opts = opts || {};
//         this.opts = Object.assign({}, opts);
//         this.opts.formatter = Object.assign({
//             patientId: value => "" + value,
//             lastName: identity,
//             firstName: identity,
//             lastNameYomi: identity,
//             firstNameYomi: identity,
//             birthday: data => data.kanji,
//             sex: data => data.kanji,
//             address: identity,
//             phone: identity,
//             age: age => "" + age
//         }, opts.formatter);
//         this.patientId = map.patientId;
//         this.patientLastName = map.lastName;
//         this.patientFirstName = map.firstName;
//         this.patientLastNameYomi = map.lastNameYomi;
//         this.patientFirstNameYomi = map.firstNameYomi;
//         this.patientBirthday = map.birthday;
//         this.patientSex = map.sex;
//         this.patientAddress = map.address;
//         this.patientPhone = map.phone;
//         this.patientAge = map.age;
//     }
//
//     set(ele, value, formatter){
//         if( ele ){
//             if( formatter ){
//                 value = formatter(value);
//             }
//             ele.text(value);
//         }
//     }
//
//     clearField(ele){
//         if( ele ){
//             ele.text("");
//         }
//     }
//
//     clear(){
//         this.clearField(this.patientId);
//         this.clearField(this.patientLastName);
//         this.clearField(this.patientFirstName);
//         this.clearField(this.patientLastNameYomi);
//         this.clearField(this.patientFirstNameYomi);
//         this.clearField(this.patientBirthday);
//         this.clearField(this.patientSex);
//         this.clearField(this.patientAddress);
//         this.clearField(this.patientPhone);
//         this.clearField(this.patientAge);
//     }
//
//     setPatient(patient){
//         if( patient ){
//             let formatter = this.opts.formatter;
//             this.set(this.patientId, patient.patientId, formatter.patientId);
//             this.set(this.patientLastName, patient.lastName, formatter.lastName);
//             this.set(this.patientFirstName, patient.firstName, formatter.firstName);
//             this.set(this.patientLastNameYomi, patient.lastNameYomi, formatter.lastNameYomi);
//             this.set(this.patientFirstNameYomi, patient.firstNameYomi, formatter.firstNameYomi);
//             this.set(this.patientBirthday, birthdayData(patient.birthday), formatter.birthday);
//             this.set(this.patientSex, sexData(patient.sex), formatter.sex);
//             this.set(this.patientAddress, patient.address, formatter.address);
//             this.set(this.patientPhone, patient.phone, formatter.phone);
//             this.set(this.patientAge, ageData(patient.birthday), formatter.age);
//         } else {
//             this.clear();
//         }
//     }
// }
//
// function birthdayData(birthday){
//     return kanjidate.sqldateToData(birthday);
// }
//
// let sexDataMap = {
//     "M": {alpha: "Male", kanji: "男"},
//     "F": {alpha: "Female", kanji: "女"}
// };
//
// function sexData(sex){
//     return sexDataMap[sex];
// }
//
// function ageData(birthday){
//     return kanjidate.calcAge(birthday);
// }
//
