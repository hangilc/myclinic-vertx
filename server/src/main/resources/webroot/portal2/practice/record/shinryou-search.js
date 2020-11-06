import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";

let html = `
<h3>診療行為検索</h3>
<form class="x-form">
    <input type="text" class="x-search-text search-text">
    <button type="submit">検索</button>
</form>
<div class="search-result">
    <select size="10" class="x-select"></select>
</div>
<div class="command-box">
    <button class="x-enter">入力</button>
    <button class="x-close">閉じる</button>
</div>
`;

export function createShinryouSearch(visitId, visitedAt, rest){
    let ele = document.createElement("div");
    ele.classList.add("workarea", "shinryou-search");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.close.onclick = event => ele.remove();
    map.form.onsubmit = async event => {
        event.preventDefault();
        let text = map.searchText.value;
        let result = await rest.searchShinryouMaster(text, visitedAt);
        map.select.innerHTML = "";
        for(let master of result){
            let opt = F.createOption(master.name, master);
            map.select.append(opt);
        }
    };
    map.enter.onclick = async event => {
        let opt = map.select.querySelector("option:checked");
        if( opt ){
            let master = opt.data;
            let s = {
                visitId: visitId,
                shinryoucode: master.shinryoucode
            };
            let sid = await rest.enterShinryou(s);
            let sf = await rest.getShinryouFull(sid);
            ele.dispatchEvent(F.event("batch-entered", {shinryouFulls: [sf]}));
        }
    };
    return ele;
}

