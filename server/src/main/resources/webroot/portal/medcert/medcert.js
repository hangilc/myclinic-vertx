import {parseElement} from "../../js/parse-node.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {SelectPatientDialog} from "../../components/select-patient-dialog.js";
import * as kanjidate from "../../js/kanjidate.js";
import {SavedPdfWidget} from "../../components/saved-pdf-widget.js";
import * as patientImage from "../../js/patient-image.js";

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
    
    <div class="mb-2 x-saved-pdf-wrapper"></div>
    
    <div>
        <button type="button" class="btn btn-primary x-create">作成</button>
    </div>
</div>
`;


export class MedCert {
    constructor(rest) {
        this.rest = rest;
        this.patient = null;
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
            this.patient = patient;
        }
    }

    imagePathToUrl(path){
        return this.rest.url("/show-pdf", {file: path});
    }

    async doCreate() {
        let data = await this.collectData();
        let savedPath = await this.rest.renderMedCert(data);
        let url = this.imagePathToUrl(savedPath);
        let box = new SavedPdfWidget(url);
        box.setTitle("診断書PDF");
        box.enableStamp(async () => {
            let newPath = await this.doStamp(savedPath);
            box.setImageUrl(this.imagePathToUrl(newPath));
            savedPath = newPath;
        });
        box.enableSave(async () => {
            let patientId = this.patient ? this.patient.patientId : 0;
            let file = patientImage.createPatientImageFileName(patientId, "medcert", ".pdf");
            let targetFile = await this.rest.savedPatientImageToken(patientId, file);
            await this.rest.copyFile(savedPath, targetFile, true);
            alert("診断書ファイルを保存しました。");
            box.close();
        });
        box.ele.addEventListener("close", async event => {
            await this.rest.deleteFile(savedPath);
        });
        this.map.savedPdfWrapper.prepend(box.ele);
    }

    async doStamp(srcFile){
        let dstFile = await this.rest.createTempFileName("medcert-stamped-", ".pdf");
        await this.rest.putStampOnPdf(srcFile, "medcert-gray", dstFile);
        await this.rest.deleteFile(srcFile);
        return dstFile;
    }

    async collectData() {
        let clinicInfo = await this.rest.getClinicInfo();
        return {
            patientName: this.inputValue("name"),
            birthDate: this.inputValue("birthDate"),
            diagnosis: this.inputValue("diagnosis"),
            text: this.inputValue("text"),
            issueDate: this.inputValue("issueDate"),
            postalCode: clinicInfo.postalCode,
            address: clinicInfo.address,
            phone: "Tel: " + clinicInfo.tel,
            fax: "Fax: " + clinicInfo.fax,
            clinicName: clinicInfo.name,
            doctorName: clinicInfo.doctorName
        };
    }

    inputValue(name) {
        return this.map[name].value;
    }

}

