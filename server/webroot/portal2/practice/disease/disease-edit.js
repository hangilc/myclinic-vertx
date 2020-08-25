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
    <form onsubmit="return false;" class="x-search-form">
        <input type="text" class="x-search-text">
        <button type="submit">検索</button>
        <a href="javascript:void(0)" class="x-examples-link">例</a>
        <div>
            <input type="radio" name="search-kind" value="byoumei" checked> 病名
            <input type="radio" name="search-kind" value="adj"> 修飾語
        </div>
    </form>
    <select class="search-result x-search-result" size="10"></select>
</div>
`;

export function createDiseaseEdit(diseaseFull, rest){
    let ele = document.createElement("div");
    ele.innerHTML = html;
    let map = parseElement(ele);
    let byoumeiMaster = diseaseFull.master;
    let adjMasters = diseaseFull.adjList.map(adj => adj.master);
    updateName();
    setDisp(map, diseaseFull.disease);
    map.examplesLink.onclick = async event => {
        await F.fillSelectWithDiseaseExampleOptions(map.searchResult, rest);
    };
    map.searchForm.onsubmit = async event => {
        event.preventDefault();
        let at = getStartDate();
        if( !at ){
            alert("検索のために、開始日の設定が必要です。");
            return;
        }
        let text = getSearchText();
        let searchKind = getSearchKind();
        await F.searchDisease(text, at, searchKind, map.searchResult, rest);
    };
    F.setupSearchDiseaseResultHandler(map.searchResult,
        argByoumeiMaster => {
            byoumeiMaster = argByoumeiMaster;
            updateName();
        },
        argShuushokugoMaster => {
            adjMasters.push(argShuushokugoMaster);
            updateName();
        },
        async argExample => {
            let ms = await F.resolveMastersOfDiseaseExample(argExample, getStartDate(), rest);
            byoumeiMaster = ms.byoumeiMaster;
            adjMasters = ms.adjMasters;
            updateName();
        });
    map.enter.onclick = async event => {
        await doEnter(diseaseFull.disease.diseaseId, diseaseFull.disease.patientId,
            byoumeiMaster, adjMasters, map, rest);
        let diseaseId = diseaseFull.disease.diseaseId;
        let updated = await rest.getDisease(diseaseId);
        ele.dispatchEvent(F.event("disease-updated", updated));
    };
    map.addSusp.onclick = event => {
        adjMasters.push(F.getAdjMasterSusp());
        updateName();
    }
    map.deleteAdj.onclick = event => {
        adjMasters = [];
        updateName();
    };
    map.clearEndDate.onclick = event => {
        map.endDate.value = "";
    };
    map.deleteDisease.onclick = async event => {
        if( confirm("この病名を削除していいですか？") ){
            let diseaseId = diseaseFull.disease.diseaseId;
            await rest.deleteDisease(diseaseId);
            ele.dispatchEvent(F.event("disease-deleted", diseaseId));
        }
    };
    return ele;

    function updateName(){
        map.name.innerText = DiseaseUtil.diseaseRepByMasters(byoumeiMaster, adjMasters);
    }

    function getSearchText(){
        return map.searchText.value;
    }

    function getSearchKind(){
        return map.searchForm.querySelector("input[type=radio][name='search-kind']:checked").value;
    }

    function getStartDate(){
        return map.startDate.value;
    }
}

async function doEnter(diseaseId, patientId, byoumeiMaster, adjMasters, map, rest){
    let req = F.composeModifyDiseaseReq(diseaseId, patientId, byoumeiMaster.shoubyoumeicode,
        getStartDate(map), getEndReason(map), getEndDate(map),
        adjMasters.map(m => m.shuushokugocode));
    await rest.modifyDisease(req);
}

function getStartDate(map){
    return map.startDate.value;
}

function getEndReason(map){
    return map.endReasonSelect.value;
}

function getEndDate(map){
    let d = map.endDate.value;
    return d || "0000-00-00";
}

function setDisp(map, disease){
    let d = disease;
    map.startDate.value = d.startDate;
    if( d.endDate === "0000-00-00" ){
        map.endDate.value = "";
    } else {
        map.endDate.value = d.endDate;
    }
    map.endReasonSelect.value = d.endReason;
}