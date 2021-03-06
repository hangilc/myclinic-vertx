import {parseElement} from "../../js/parse-node.js";
import {MeisaiDialog} from "./meisai-dialog.js";
import {click} from "../../js/dom-helper.js";
import {SearchTextForPatientDialog} from "./search-text-for-patient-dialog.js";
import {UploadImageDialog} from "./upload-image-dialog.js";
import {UploadProgress} from "./upload-progress.js";
import {PatientImageList} from "../../components/patient-image-list.js";
import * as prop from "./app.js";
import {createElementFrom} from "../../js/create-element-from.js";

let tmpl = `
    <div>
        <button class="x-cashier btn btn-secondary">会計</button>
        <button class="x-end  ml-2 btn btn-secondary">患者終了</button>
        <a href="javascript:void(0)" class="x-register-current  ml-2">診察登録</a>
        <a href="javascript:void(0)" class="x-search-text  ml-2">文章検索</a>
        <a href="javascript:void(0)" class="x-upload-image ml-2">画像保存</a>
        <a href="javascript:void(0)" class="x-list-image ml-2">画像一覧</a>
    </div>
`;

export class PatientManip {
    constructor() {
        this.prop = prop;
        this.rest = prop.rest;
        this.ele = createElementFrom(tmpl);
        this.manipWorkarea = prop.map.patientManipWorkarea;
        this.generalWorkarea = prop.map.generalWorkarea;
        let map = parseElement(this.ele);
        click(map.cashier, async event => await this.doCashier());
        click(map.end, event => this.prop.endSession());
        click(map.registerCurrent, async event => await this.doRegisterCurrent());
        click(map.searchText, async event => await this.doSearchText());
        click(map.uploadImage, async event => await this.doUploadImage());
        click(map.listImage, async event => await this.doListImage());
    }

    async doCashier(){
        let visitId = this.prop.currentVisitId;
        if (! (visitId > 0) ) {
            alert("現在診察中ではないので、会計はできません。");
            return;
        }
        let meisai = await this.prop.rest.getMeisai(visitId);
        let dialog = new MeisaiDialog(meisai);
        let result = await dialog.open();
        if (result) {
            await rest.endExam(visitId, result.charge);
            this.prop.endSession();
        }
    }

    async doRegisterCurrent() {
        let patient = this.prop.patient;
        if( !patient ){
            alert("患者が選択されていません。");
            return;
        }
        if( !confirm("新規診察を登録しますか？") ){
            return;
        }
        let patientId = patient.patientId;
        let visitId = await this.rest.startVisit(patientId);
        let currentVisitId = this.prop.currentVisitId;
        this.prop.endSession();
        await this.prop.startSession(patientId, currentVisitId > 0 ? currentVisitId : visitId);
    }

    async doSearchText() {
        let dialog = new SearchTextForPatientDialog(this.prop);
        await dialog.open(() => dialog.initFocus());
    }

    async doUploadImage() {
        const patient = this.prop.patient;
        if( !patient ){
            alert("Patient not selected");
            return;
        }
        const patientId = patient.patientId;
        if (patientId > 0) {
            let dialog = new UploadImageDialog(patientId);
            let uploaders = await dialog.open();
            if (uploaders) {
                let reporter = new UploadProgress(uploaders);
                this.generalWorkarea.append(reporter.ele);
            }
        }
    }

    async doListImage() {
        const patient = this.prop.patient;
        if( !patient ){
            alert("Patient not selected");
            return;
        }
        const patientId = patient.patientId;
        if (patientId > 0) {
            let w = new PatientImageList(rest, true);
            await w.init(patientId);
            let wrapper = this.manipWorkarea;
            wrapper.prepend(w.ele);
        }
    }
}