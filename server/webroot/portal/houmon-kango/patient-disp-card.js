import {Component, parseElement} from "./component.js";
import {PatientDisp} from "./patient-disp.js";
import * as kanjidate from "../js/kanjidate.js";

let template = `
<div class="card">
    <div class="card-header x-title"></div>
    <div class="card-body">
        <div class="row x-disp_">
            <div class="col-sm-3">患者番号</div>
            <div class="col-sm-9 x-patient-id"></div>
            <div class="col-sm-3">氏名</div>
            <div class="col-sm-9">
                <span class="x-last-name"></span>
                <span class="x-first-name ml-2"></span>
            </div>
            <div class="col-sm-3">よみ</div>
            <div class="col-sm-9">
                <span class="x-last-name-yomi"></span>
                <span class="x-first-name-yomi ml-2"></span>
            </div>
            <div class="col-sm-3">生年月日</div>
            <div class="col-sm-9">
                <span class="x-birthday" data-format="kanji"></span>生
                （<span class="x-age"></span>才）
            </div>
            <div class="col-sm-3">性別</div>
            <div class="col-sm-9 x-sex" data-format="kanji"></div>
            <div class="col-sm-3">住所</div>
            <div class="col-sm-9 x-address"></div>
            <div class="col-sm-3">電話</div>
            <div class="col-sm-9 x-phone"></div>
        </div>
    </div>
</div>
`;

export class PatientDispCard extends Component {

    constructor(patient){
        super($(template));
        let map = parseElement(this.ele);
        map.title.text(patient.lastName + patient.firstName);
        this.patientDisp = new PatientDisp(map.disp);
        this.ageElement = map.disp.age;
        this.set(patient);
    }

    set(patient){
        this.patientDisp.set(patient);
        this.ageElement.text(kanjidate.calcAge(patient.birthday));
    }

}
