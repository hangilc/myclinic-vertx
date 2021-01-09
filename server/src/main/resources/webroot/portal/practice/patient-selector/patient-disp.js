import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import * as kanjidate from "../../../js/kanjidate.js";
import {sexToRep} from "../../../js/consts.js";

let tmpl = `
    <div class="card">
        <div class="card-body">
            <div class="row">
                <div class="col-sm-4">患者番号</div>
                <div class="col-sm-8 x-patient-id"></div>
                <div class="col-sm-4">氏名</div>
                <div class="col-sm-8">
                    <span class="x-last-name"></span><span
                        class="x-first-name ml-2"></span>
                </div>
                <div class="col-sm-4">よみ</div>
                <div class="col-sm-8 x-yomi">
                    <span class="x-last-name-yomi"></span><span
                        class="x-first-name-yomi ml-2"></span>
                </div>
                <div class="col-sm-4">生年月日</div>
                <div class="col-sm-8 x-birthday"></div>
                <div class="col-sm-4">性別</div>
                <div class="col-sm-8 x-sex"></div>
                <div class="col-sm-4">住所</div>
                <div class="col-sm-8 x-address"></div>
                <div class="col-sm-4">電話</div>
                <div class="col-sm-8 x-phone"></div>
            </div>
        </div>
    </div>
`;

export class PatientDisp {
    constructor(){
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
    }

    setPatient(patient){
        let map = this.map;
        map.patientId.innerText = patient.patientId;
        map.lastName.innerText = patient.lastName;
        map.firstName.innerText = patient.firstName;
        map.lastNameYomi.innerText = patient.lastNameYomi;
        map.firstNameYomi.innerText = patient.firstNameYomi;
        map.birthday.innerText = kanjidate.sqldateToKanji(patient.birthday);
        map.sex.innerText = sexToRep(patient.sex, "性");
        map.address.innerText = patient.address;
        map.phone.innerText = patient.phone;
    }
}