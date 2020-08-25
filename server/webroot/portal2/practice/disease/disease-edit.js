import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";
import * as DiseaseUtil from "../../../portal/js/disease-util.js";

let html = `
<div class="show">
    <div>名前：<span class="x-name"></span></div>
    <div>開始日：<input type="date" class="x-start-date"></div>
    <div>終了日：<input type="date" class="x-end-date"></div>
    <form onsubmit="return false;" class="x-end-reason-form">
            <input type="radio" name="end-reason" value="N">継続</input>
            <input type="radio" name="end-reason" value="C">治癒</input>
            <input type="radio" name="end-reason" value="S">中止</input>
            <input type="radio" name="end-reason" value="D">死亡</input>
    </form>
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
    ele.classList.add("disease-edit");
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
    map.startDate.onchange = async event => {
        let at = getStartDate();
        if( !at ){
            return;
        }
        let err = await F.confirmDiseaseMasters(byoumeiMaster, adjMasters, at, rest);
        if( err ){
            alert(err);
        }
    };
    map.endDate.onchange = async event => {
        let at = getEndDate();
        if( !at ){
            return;
        }
        let err = await F.confirmDiseaseMasters(byoumeiMaster, adjMasters, at, rest);
        if( err ){
            alert(err);
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
    return map.endReasonForm.querySelector("input[type='radio']:checked").value;
}

function setEndReason(map, endReason){
    map.endReasonForm.querySelector(`input[type='radio'][value='${endReason}']`).checked = true;
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
    setEndReason(map, d.endReason);
}