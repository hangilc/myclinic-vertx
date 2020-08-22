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
        <a href="javascript:void(0)">例</a>
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
    map.searchForm.onsubmit = async event => {
        event.preventDefault();
        let at = getStartDate();
        if( !at ){
            alert("検索のために、開始日の設定が必要です。");
            return;
        }
        let text = getSearchText();
        let searchKind = getSearchKind();
        if( searchKind === "byoumei" ){
            let masters = await rest.searchByoumeiMaster(text, at);
            map.searchResult.innerHTML = "";
            masters.forEach(m => {
                let opt = F.createOption(m.name, m);
                opt.dataset.kind = "byoumei";
                map.searchResult.append(opt);
            });
        } else if( searchKind === "adj" ){
            let masters = await rest.searchShuushokugoMaster(text, at);
            map.searchResult.innerHTML = "";
            masters.forEach(m => {
                let opt = F.createOption(m.name, m);
                opt.dataset.kind = "adj";
                map.searchResult.append(opt);
            });
        }
    };
    map.searchResult.onchange = event => {
        let opt = map.searchResult.querySelector("option:checked");
        if( opt ){
            let kind = opt.dataset.kind;
            if( kind === "byoumei" ){
                byoumeiMaster = opt.data;
                updateName();
            } else if( kind ==="adj" ){
                adjMasters.push(opt.data);
                updateName();
            }
        }
    };
    map.enter.onclick = event => {

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