import {Component, parseElement} from "./component.js";
import {PatientSelectDialog} from "./patient-select-dialog.js"
import * as kanjidate from "../js/kanjidate.js";
import {PatientDispCard} from "./patient-disp-card.js";

// language=HTML
let template = `
<div>
    <h3 id="houmon-kango-leader">訪問看護指示書</h3>
    
    <div class="form-inline">
        <span>開始日</span>
        <input type="date" class="form-control ml-2 x-from-date"/>
        <span class="ml-4">終了日</span>
        <input type="date" class="form-control ml-2 x-upto-date"/>
    </div>
    
    <div class="form-inline mt-2">
        <span>発行日</span>
        <input type="date" class="form-control ml-2 x-issue-date"/>
    </div>
    
    <div class="form-inline mt-2">
        <span>送付先</span>
        <input type="text" class="form-control ml-2 x-recipient"/>
    </div>
    
    <div class="mt-2">
        <button type="button" class="btn btn-secondary x-select-patient">患者選択
        </button>
        <button type="button" class="btn btn-secondary ml-2"
                id="houmon-kango-end-patient">患者終了
        </button>
    </div>
    <div class="my-4 x-patient"></div>
    
    <div class="form">
        <textarea class="form-control x-data" rows="10"></textarea>
    </div>
    
    <div class="form-inline mt-2 x-create-shijisho-form">
        <button type="button"
                class="btn btn-primary x-create-shijisho">指示書作成
        </button>
        <button type="button"
                class="btn btn-secondary ml-2 x-save-history">保存
        </button>
        <a href="/integration/houmon-kango/list-params"
           class="btn btn-link form-control ml-2"
           target="_blank">属性一覧</a>
<!--        <input type="text" name="data" formenctype="text/plain" class="d-none"/>-->
    </div>
    
    <div class="houmon-kango-history mt-3">
        <h5>履歴</h5>
        <table class="table">
            <thead>
            <tr>
                <th>送付先</th>
                <th>開始日</th>
                <th>終了日</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody></tbody>
        </table>
    </div>
</div>
`;

export class HoumonKango extends Component {

    constructor(rest, integration, clinicInfo){
        super($(template));
        this.rest = rest;
        this.integration = integration;
        this.clinicInfo = clinicInfo;
        let map = parseElement(this.ele);
        this.fromDateElement = map.fromDate;
        this.uptoDateElement = map.uptoDate;
        this.issueDateElement = map.issueDate;
        this.recipientElement = map.recipient;
        this.patientElement = map.patient;
        this.dataElement = map.data;
        this.createShijihoElement = map.createShijisho;
        this.createShijishoForm = map.createShijishoForm;
        this.selectPatientElement = map.selectPatient;
        this.init();
    }

    init(){
        this.fromDateElement.on("change", event => this.updateDataFromDate());
        this.uptoDateElement.on("change", event => this.updateDataUptoDate());
        this.issueDateElement.on("change", event => this.updateDataIssueDate());
        this.recipientElement.on("change", event => this.updateDataRecipient());
        this.selectPatientElement.on("click", event => this.doSelectPatient());
        this.setData(clinicInfoToData(this.clinicInfo));
        this.issueDateElement.val(kanjidate.todayAsSqldate()).trigger("change");
        this.createShijihoElement.on("click", event => this.doCreateShijisho());
    }

    async doCreateShijisho(){
        let data = this.getData();
        let result = await this.integration.createShijisho(data);
        window.open("/portal/tmp/" + result, "_blank");
    }

    async doSelectPatient(){
        let dialog = new PatientSelectDialog(this.rest);
        let result = await dialog.open();
        if( result ){
            this.patient = result;
            this.updatePatientCard();
            this.updateDataPatient();
        }
    }

    updatePatientCard(){
        let patient = this.patient;
        if( patient ){
            let card = new PatientDispCard(patient);
            card.appendTo(this.patientElement);
        } else {
            this.patientElement.html("");
        }
    }

    setData(data){
        this.dataElement.val(JSON.stringify(data, null, 2));
    }

    getData(){
        return JSON.parse(this.dataElement.val());
    }

    updateDataFromDate(){
        let date = this.fromDateElement.val();
        let data = this.getData();
        if( date ){
            let dateData = kanjidate.sqldateToData(date);
            data["subtitle1.from.nen"] = "" + dateData.nen;
            data["subtitle1.from.month"] = "" + dateData.month;
            data["subtitle1.from.day"] = "" + dateData.day;
        } else {
            data["subtitle1.from.nen"] = "";
            data["subtitle1.from.month"] = "";
            data["subtitle1.from.day"] = "";
        }
        this.setData(data);
    }

    updateDataUptoDate(){
        let date = this.uptoDateElement.val();
        let data = this.getData();
        if( date ){
            let dateData = kanjidate.sqldateToData(date);
            data["subtitle1.upto.nen"] = "" + dateData.nen;
            data["subtitle1.upto.month"] = "" + dateData.month;
            data["subtitle1.upto.day"] = "" + dateData.day;
        } else {
            data["subtitle1.upto.nen"] = "";
            data["subtitle1.upto.month"] = "";
            data["subtitle1.upto.day"] = "";
        }
        this.setData(data);
    }

    updateDataIssueDate(){
        let date = this.issueDateElement.val();
        let data = this.getData();
        if( date ){
            let dateData = kanjidate.sqldateToData(date);
            data["issue-date.nen"] = "" + dateData.nen;
            data["issue-date.month"] = "" + dateData.month;
            data["issue-date.day"] = "" + dateData.day;
        } else {
            data["issue-date.nen"] = "";
            data["issue-date.month"] = "";
            data["issue-date.day"] = "";
        }
        this.setData(data);
    }

    updateDataRecipient(){
        let text = this.recipientElement.val();
        let data = this.getData();
        data.recipient = text;
        this.setData(data);
    }

    updateDataPatient(){
        let data = this.getData();
        for (let key of ["birthday.gengou.meiji", "birthday.gengou.taishou",
            "birthday.gengou.shouwa", "birthday.gengou.heisei"]) {
            delete data[key];
        }
        let patient = this.patient;
        if( patient ){
            data["shimei"] = `${patient.lastName}${patient.firstName}`;
            let birthday = kanjidate.sqldateToData(patient.birthday);
            switch (birthday.gengou.name) {
                case "明治":
                    data["birthday.gengou.meiji"] = true;
                    break;
                case "大正":
                    data["birthday.gengou.taishou"] = true;
                    break;
                case "昭和":
                    data["birthday.gengou.shouwa"] = true;
                    break;
                case "平成":
                    data["birthday.gengou.heisei"] = true;
                    break;
            }
            data["birthday.nen"] = birthday.nen;
            data["birthday.month"] = birthday.month;
            data["birthday.day"] = birthday.day;
            data["age"] = kanjidate.calcAge(patient.birthday);
            data["address"] = patient.address;
        } else {
            delete data["shimei"];
            delete data["birthday.nen"];
            delete data["birthday.month"];
            delete data["birthday.day"];
            delete data["age"];
            delete data["address"];
        }
        this.setData(data);
    }

}

function clinicInfoToData(clinicInfo){
    return {
        "clinic.name": clinicInfo.name,
        "clinic.address": clinicInfo.postalCode + " " + clinicInfo.address,
        "clinic.phone": clinicInfo.tel,
        "clinic.fax": clinicInfo.fax,
        "doctor-name": clinicInfo.doctorName
    };
}
