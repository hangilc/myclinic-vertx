import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";
import * as DiseaseUtil from "../../../portal/js/disease-util.js";

let html = `
<div>
    <div>名前：<span class="x-name"></span></div>
    <div><input type="date" class="x-start-date"></div>
    <div>から</div>
    <div><input type="date" class="x-end-date"></div>
    <div>
        <select class="x-end-reason-select">
            <option value="N">継続</option>
            <option value="C" checked>治癒</option>
            <option value="S">中止</option>
            <option value="D">死亡</option>
        </select>
    </div>
</div>
<div>
    <button class="x-enter">入力</button>
    <a href="javascript:void(0)" class="x-add-susp">の疑い</a> |
    <a href="javascript:void(0)" class="x-delete-adj">修飾語削除</a> |
    <a href="javascript:void(0)" class="x-clear-end-date">終了日クリア</a> |
    <a href="javascript:void(0)" class="x-delete-disease">削除</a>
</div>
<div>
    <form onsubmit="return false;">
        <input type="text">
        <button type="submit">検索</button>
        <a href="javascript:void(0)">例</a>
        <div>
            <input type="radio" name="search-kind" value="byoumei"> 病名
            <input type="radio" name="search-kind" value="adj"> 修飾語
        </div>
    </form>
    <select class="search-result x-search-result" size="10"></select>
</div>
`;

export function createDiseaseEdit(diseaseFull){
    let ele = document.createElement("div");
    ele.innerHTML = html;
    let map = parseElement(ele);
    setDisp(map, diseaseFull);
    return ele;
}

function setDisp(map, diseaseFull){
    let d = diseaseFull.disease;
    map.name.innerText = DiseaseUtil.diseaseRep(diseaseFull);
    map.startDate.value = d.startDate;
    if( d.endDate === "0000-00-00" ){
        map.endDate.value = "";
    } else {
        map.endDate.value = d.endDate;
    }
    map.endReasonSelect.value = d.endReason;
}