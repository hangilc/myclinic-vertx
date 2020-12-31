import {Dialog} from "../../../js/dialog2.js";
import {parseElement} from "../../../js/parse-node.js";
import {click, submit} from "../../../js/dom-helper.js";

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
        this.setTitle("患者検索");
        this.getBody().innerHTML = bodyTmpl;
        let bmap = this.bmap = parseElement(this.getBody());
        submit(bmap.form, async event => await this.doSearch());
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
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
}