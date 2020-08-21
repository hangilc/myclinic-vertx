import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";
import * as DiseaseUtil from "../../../portal/js/disease-util.js";

let html = `
<div>
    <div>名称：<span class="x-name"></span></div>
    <div>開始日：<span class="x-start-date"></span></div>
    <div>転機：<span class="x-end-reason"></span></div>
    <div>終了日：<span class="x-end-date"></span></div>
</div>
<div>
    <button class="x-edit-button">編集</button>
</div>
<div>
    <select class="x-select" size="16"></select>
</div>
`;

export function createDiseaseSelect(diseaseFulls){
    let ele = document.createElement("div");
    ele.innerHTML = html;
    let map = parseElement(ele);
    let selected = null;
    diseaseFulls.forEach(df => {
        let rep = DiseaseUtil.diseaseFullRep(df);
        let opt = F.createOption(rep, df);
        map.select.append(opt);
    });
    map.select.onchange = event => {
        let opt = map.select.querySelector("option:checked");
        if( opt ){
            let df = opt.data;
            selected = df;
            setDisp(map, df);
        }
    };
    map.editButton.onclick = event => {
        if( selected ){
            ele.dispatchEvent(F.event("disease-selected", selected));
        }
    };
    return ele;
}

function setDisp(map, diseaseFull){
    let d = diseaseFull.disease;
    map.name.innerText = DiseaseUtil.diseaseRep(diseaseFull);
    map.startDate.innerText = F.formatDatePadded(new Date(d.startDate));
    map.endReason.innerText = F.diseaseEndReasonToRep(d.endReason);
    if( d.endDate === "0000-00-00" ){
        map.endDate.innerText = "";
    } else {
        map.endDate.innerText = F.formatDatePadded(new Date(d.endDate));
    }
}