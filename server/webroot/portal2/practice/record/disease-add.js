import {parseElement} from "../../js/parse-element.js";

let html = `
<div>
    <div class="disease-name">名称：<span class="x-disease-name"></span></div>
    <div>
        開始日：<input type="date" class="x-start-date">
    </div>
    <div>
        <button>入力</button>
        <a href="javascript:void(0)">の疑い</a> |
        <a href="javascript:void(0)">修飾語削除</a>
    </div>
    <div>
        <form class="x-form">
            <div>
                <input type="text" class="x-search-text">
                <button type="submit">検索</button>
                <a href="javascript:void(0)">例</a>
            </div>
            <div> 
                <input type="radio" name="search-mode" value="name" checked>病名
                <input type="radio" name="search-mode" value="adj">修飾語
            </div>
        </form>
        <select size="12"></select>
    </div>
</div>
`;

export function createDiseaseAdd(diseaseFulls, visitedAt, rest){
    let ele = document.createElement("div");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.startDate.value = visitedAt;
    map.form.onsubmit = async event => {
        event.preventDefault();
        let text = map.searchText.value;
        let result = await search(text, getSearchMode(), visitedAt, rest);
    };
    return ele;

    function getSearchMode(){
        return ele.querySelector("form input[type=radio][name=search-mode]:checked").value;
    }
}

async function search(text, mode, at, rest){
    if( mode === "name" ){
        let result = await searchByoumei(text, at, rest);
    }
}

async function searchByoumei(text, at, rest){
    let result = await rest.searchByoumeiMaster(text, at);
    console.log(result);
}