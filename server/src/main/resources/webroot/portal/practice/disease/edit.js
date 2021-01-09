import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import * as DiseaseUtil from "../../js/disease-util.js";
import {click, on, replaceNode} from "../../../js/dom-helper.js";
import * as kanjidate from "../../js/kanjidate.js";
import * as consts from "../../../js/consts.js";
import {Modify} from "./modify.js";

const tmpl = `
    <div>
        <div class="x-panel mb-2">
            <div>名称：<span class="x-name"></span></div>
            <div>開始日：<span class="x-start-date"></span></div>
            <div>転機：<span class="x-end-reason"></span></div>
            <div>終了日：<span class="x-end-date"></span></div>
        </div>
        <div class="mb-2">
            <button type="button" class="x-edit btn btn-primary btn-sm">編集</button>
        </div>
        <select class="form-control x-select mt-1" size="6"></select>
    </div>
`;

export class Edit {
    constructor(diseases) {
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.selectedDisease = null;
        this.setDiseases(diseases);
        click(this.map.edit, event => this.doEdit());
        on(this.map.select, "change", event => this.doSelect());
    }

    setDiseases(diseases){
        const select = this.map.select;
        select.innerHTML = "";
        diseases.forEach(d => {
            const opt = createOpt(d);
            select.append(opt);
        });
    }

    setDisp(disease){
        this.map.name.innerText = DiseaseUtil.diseaseRep(disease);
        this.map.startDate.innerText = formatDate(disease.disease.startDate);
        this.map.endReason.innerText = consts.diseaseEndReasonToKanji(disease.disease.endReason);
        this.map.endDate.innerText = formatDate(disease.disease.endDate);
    }

    doEdit(){
        const disease = this.selectedDisease;
        if( !disease ){
            alert("病名が選択されていません。");
            return;
        }
        const modify = new Modify(disease);
        replaceNode(this.ele, modify.ele);
    }

    doSelect(){
        const opt = this.map.select.querySelector("option:checked");
        if( opt ){
            const disease = opt.data;
            this.selectedDisease = disease;
            this.setDisp(disease);
        }
    }
}

function createOpt(diseaseFull){
    let opt = document.createElement("option");
    opt.innerText = DiseaseUtil.diseaseFullRep(diseaseFull);
    opt.data = diseaseFull;
    return opt;
}

function formatDate(date){
    if( !date ) {
        return "";
    } else if( date === "0000-00-00" ){
        return "";
    } else {
        return kanjidate.sqldateToKanji(date);
        // let data = kanjidate.sqldateToData(date);
        // return `${data.gengou.name}${data.nen}年${data.month}月${data.day}日`;
    }
}

