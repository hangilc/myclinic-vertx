import {Dialog} from "../../../js/dialog2.js";
import {parseElement} from "../../../js/parse-node.js";
import {click, submit, on} from "../../../js/dom-helper.js";
import * as kanjidate from "../../../js/kanjidate.js";
import {sexToRep} from "../../js/consts.js";

let bodyTmpl = `
    <div class="row">
        <div class="col-6">
            <div>
                <form class="form-inline x-form" onsubmit="return false;">
                    <input class="form-control x-input"/>
                    <button type="submit" class="form-control ml-2">検索</button>
                </form>
                <select class="form-control mt-2 form-control x-select" size="7"></select>
            </div>
        </div>
        <div class="col-6">
            <div class="card mt-2">
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
        </div>
    </div>
`;

let footerTmpl = `
    <button type="button" class="btn btn-primary x-register-enter">受付・診察</button>
    <button type="button" class="btn btn-primary x-enter">選択</button>
    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class SearchDialog extends Dialog {
    constructor(prop) {
        super();
        this.prop = prop;
        this.rest = prop.rest;
        this.patient = null;
        this.setTitle("患者検索");
        this.getBody().innerHTML = bodyTmpl;
        let bmap = this.bmap = parseElement(this.getBody());
        submit(bmap.form, async event => await this.doSearch());
        on(bmap.select, "change", event => this.doSelect());
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.registerEnter, async event => await this.doRegisterEnter());
        click(fmap.enter, event => this.doEnter());
        click(fmap.cancel, event => this.close());
    }

    initFocus(){
        this.bmap.input.focus();
    }

    async doSearch(){
        let text = this.bmap.input.value.trim();
        if( !text ){
            return;
        }
        let patients = await this.rest.searchPatient(text);
        this.setSearchResult(patients);
    }

    setSearchResult(patients){
        let select = this.bmap.select;
        select.innerHTML = "";
        patients.forEach(patient => {
            let opt = document.createElement("option");
            let patientIdRep = ("" + patient.patientId).padStart(4, "0");
            opt.innerText = `(${patientIdRep}) ${patient.lastName} ${patient.firstName}`;
            opt.data = patient;
            select.append(opt);
        });
    }

    doSelect(){
        let opt = this.bmap.select.querySelector("option:checked");
        console.log("opt", opt);
        if( opt ){
            let patient = opt.data;
            this.setPatient(patient);
        }
    }

    setPatient(patient){
        this.patient = patient;
        let map = this.bmap;
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

    async doRegisterEnter(){
        let patient = this.patient;
        if( !patient ){
            alert("患者が選択されていません。");
            return;
        }
        let visitId = await this.rest.startVisit(patient.patientId);
        this.prop.endSession();
        this.prop.startSession(patient.patientId, visitId);
        this.close();
    }

    doEnter(){
        let patient = this.patient;
        if( !patient ){
            alert("患者が選択されていません。");
            return;
        }
        this.prop.endSession();
        this.prop.startSession(patient.patientId, 0);
        this.close();
    }
}