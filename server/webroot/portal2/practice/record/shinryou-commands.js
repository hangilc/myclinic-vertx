import {parseElement} from "../../js/parse-element.js";
import {createDropdown} from "../../comp/dropdown.js";
import {createShinryouSearch} from "./shinryou-search.js";
import * as F from "../functions.js";

let html = `
<a href="javascript:void(0)" class="x-add-regular">診療行為</a> | 
<a href="javascript:void(0)" class="x-menu">その他▼</a>
`;

export function populateShinryouCommands(ele, workareaWrapper, visitId, visitedAt, rest){
    visitedAt = visitedAt.substring(0, 10);
    ele.innerHTML = html;
    let map = parseElement(ele);
    setupDropdown(map.menu, workareaWrapper, visitId, visitedAt, rest);
    map.addRegular.onclick = event => ele.dispatchEvent(F.event("add-regular-shinryou"));
}

function setupDropdown(button, workareaWrapper, visitId, visitedAt, rest){
    createDropdown(button, [
        {
            label: "検索入力",
            action: () => {
                let w = createShinryouSearch(visitId, visitedAt, rest);
                workareaWrapper.append(w);
            }
        },
        {
            label: "全部コピー",
            action: () => workareaWrapper.dispatchEvent(F.event("copy-all-shinryou", visitId))
        },
    ]);
}