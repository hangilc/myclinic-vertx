import {parseElement} from "../../js/parse-element.js";
import {getYoubi, sqldatetimeToDate} from "../../js/datetime-util.js";
import {createDropdown} from "../../comp/dropdown.js";
import * as F from "../functions.js";

let html = `
<span class="x-label label"></span>
<a href="javascript:void(0)" class="x-menu menu">▼</a>
`;

export function populateTitle(ele, visitedAt, visitId){
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.label.innerText = createLabel(visitedAt);
    setupDropdown(map.menu, visitId);
}

function setupDropdown(menu, visitId){
    createDropdown(menu, [
        {
            label: "この診察を削除",
            action: () => menu.dispatchEvent(F.event("delete-visit", visitId))
        },
        {
            label: "暫定診察に設定",
            action: () => menu.dispatchEvent(F.event("set-temp-visit-id", visitId))
        },
        {
            label: "暫定診察を削除",
            action: () => menu.dispatchEvent(F.event("release-temp-visit-id", visitId))
        },
        {
            label: "診療明細",
            action: () => menu.dispatchEvent(F.event("show-visit-meisai", visitId))
        }
    ]);
}

function createLabel(visitedAt){
    let d = sqldatetimeToDate(visitedAt);
    let month = ("" + (d.getMonth()+1)).padStart(2, "0");
    let day = ("" + d.getDate()).padStart(2, "0");
    let youbi = getYoubi(d);
    let hour = ("" + d.getHours()).padStart(2, "0");
    let min = ("" + d.getMinutes()).padStart(2, "0");
    return `${d.getFullYear()}年${month}月${day}日（${youbi}）${hour}時${min}分`;
}
