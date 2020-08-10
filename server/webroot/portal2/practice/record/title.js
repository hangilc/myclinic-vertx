import {parseElement} from "../../js/parse-element.js";
import {getYoubi} from "../../js/datetime-util.js";

let html = `
<span class="x-label label"></span>
<a href="javascript:void(0)" class="x-menu menu">▼</a>
`;

export function populateTitle(ele, visitedAt, visitId){
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.label.innerText = createLabel(visitedAt);
}

function createLabel(visitedAt){
    let d = new Date(visitedAt);
    let month = ("" + (d.getMonth()+1)).padStart(2, "0");
    let day = ("" + d.getDate()).padStart(2, "0");
    let youbi = getYoubi(d);
    let hour = ("" + d.getHours()).padStart(2, "0");
    let min = ("" + d.getMinutes()).padStart(2, "0");
    return `${d.getFullYear()}年${month}月${day}日（${youbi}）${hour}時${min}分`;
}
