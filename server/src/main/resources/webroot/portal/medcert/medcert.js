import {parseElement} from "../../js/parse-node.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {SelectPatientDialog} from "../../components/select-patient-dialog.js";
import * as kanjidate from "../../js/kanjidate.js";

let tmpl = `
<div>
    <h2>診断書作成</h2>
    
    <div class="mb-2">
        <button type="button" class="btn btn-secondary x-select-patient-button">患者選択</button>
        <button type="button" class="btn btn-link x-end-patient-button">患者終了</button>
    </div>
    
    <div class="d-table mb-2">
        <div class="d-table-row">
            <div class="d-table-cell text-right py-1 pr-2">氏名</div>
            <div class="d-table-cell py-1">
                <input class="form-control x-name">
            </div>
        </div>
        <div class="d-table-row">
            <div class="d-table-cell text-right py-1 pr-2">生年月日</div>
            <div class="d-table-cell py-1">
                <input class="form-control x-birth-date">
            </div>
        </div>
        <div class="d-table-row">
            <div class="d-table-cell text-right py-1 pr-2">診断名</div>
            <div class="d-table-cell py-1">
                 <input class="form-control x-diagnosis">
            </div>
        </div>
        <div class="d-table-row">
            <div class="d-table-cell text-right py-1 pr-2">内容</div>
            <div class="d-table-cell py-1">
                 <textarea rows="6" cols="40" class="form-control x-text"></textarea>
            </div>
        </div>
        <div class="d-table-row">
            <div class="d-table-cell text-right py-1 pr-2">発行日</div>
            <div class="d-table-cell py-1">
                 <input class="form-control x-issue-date">
            </div>
        </div>
     </div>
    <div>
        <button type="button" class="btn btn-primary x-create">作成</button>
    </div>
</div>
`;


export class MedCert {
    constructor(rest) {
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        let map = this.map = parseElement(this.ele);
        map.selectPatientButton.addEventListener("click", async event => await this.doSelectPatient());
        map.issueDate.value = kanjidate.sqldateToKanji(kanjidate.todayAsSqldate());
        map.create.addEventListener("click", event => this.doCreate());
    }

    async init() {

    }

    async doSelectPatient() {
        let dialog = new SelectPatientDialog(this.rest);
        let patient = await dialog.open();
        if (patient) {
            let map = this.map;
            map.name.value = `${patient.lastName}${patient.firstName}`;
            map.birthDate.value = kanjidate.sqldateToKanji(patient.birthday);
        }
    }

    async doCreate() {
        let data = await this.collectData();
        let savePath = await this.rest.renderMedCert(data);
        alert("Saved to " + savePath);
    }

    async collectData() {
        let clinicInfo = await this.rest.getClinicInfo();
        return {
            patientName: this.inputValue("shimei"),
            birthDate: this.inputValue("birth-date"),
            diagnosis: this.inputValue("diagnosis"),
            text: this.ele.find("textarea[name=text]").val(),
            issueDate: this.inputValue("issue-date"),
            postalCode: clinicInfo.postalCode,
            address: clinicInfo.address,
            phone: "Tel: " + clinicInfo.tel,
            fax: "Fax: " + clinicInfo.fax,
            clinicName: clinicInfo.name,
            doctorName: clinicInfo.doctorName
        };
    }

    inputValue(name) {
        return this.ele.find("input[name=" + name + "]").val();
    }

}

