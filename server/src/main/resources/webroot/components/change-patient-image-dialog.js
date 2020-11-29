import {Dialog} from "../js/dialog.js";
import {parseElement} from "../js/parse-node.js";

let bodyTmpl = `
    <div class="form-inline mb-2">
       <span class="mr-2">変更先患者ID:</span>
       <input type="text" class="form-control mr-2 x-patient-id" size="8"/>
       <button class="btn btn-secondary btn-sm x-set-patient">設定</button>
    </div>
    <div class="mb-2">
       <span class="mr-2">変更先患者氏名:</span>
       <span class="x-target-patient-label"></span>
    </div>
    <div class="form-inline mb-2">
        <span class="mr-2">ファイル名:</span>
        <input type="text" class="form-control w-auto x-file-input" />
    </div>
`;

let commandsTmpl = `
    <button class="btn btn-primary x-enter">変更実施</button>
    <button class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class ChangePatientImageDialog extends Dialog {
    constructor(srcPatientId, file, rest){
        super();
        this.srcPatientId = srcPatientId;
        this.file = file;
        this.rest = rest;
        this.setTitle("画像の患者を変更");
        this.getBody().innerHTML = bodyTmpl;
        this.bmap = parseElement(this.getBody());
        this.bmap.setPatient.addEventListener("click", async event => await this.doSetPatient());
        this.getFooter().innerHTML = commandsTmpl;
        this.cmap = parseElement(this.getFooter());
        this.cmap.enter.disabled = true;
        this.cmap.cancel.addEventListener("click", event => this.close());
    }

    async doSetPatient(){
        let text = this.bmap.patientId.value;
        if( !text ){
            alert("患者番号が入力されていません。");
            return;
        }
        let patientId = parseInt(text);
        if( isNaN(patientId) ){
            alert("患者番号の入力が不適切です。");
            return;
        }
        let patient = await this.rest.getPatient(patientId);
        this.bmap.targetPatientLabel.innerText = `${patient.lastName}${patient.firstName}`;
        this.bmap.fileInput.value = this.rewriteFileName(patient.patientId);
        this.cmap.enter.disabled = false;
    }

    rewriteFileName(targetPatientId){
        let regex = new RegExp(`^${this.srcPatientId}-`);
        return this.file.replace(regex, `${targetPatientId}-`);
    }
}