import * as kanjidate from "../../js/kanjidate.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";

let tmpl = `
    <div>
        [<span class="x-patient-id"></span>]
        <span class="x-last-name"></span><span class="x-first-name"></span>
        (<span class="x-last-name-yomi"></span><span class="x-first-name-yomi"></span>)
        <span class="x-birthday"></span>生
        (<span class="x-age"></span>)
        <span class="x-sex"></span>
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
