import {parseElement} from "../../js/parse-element.js";
import {populateTitle} from "./title.js";

let html = `
<div class="x-title title"></div>
`;

export function createRecord(visitFull, rest){
    let visit = visitFull.visit;
    let ele = document.createElement("div");
    ele.classList.add("record");
    ele.innerHTML = html;
    let map = parseElement(ele);
    populateTitle(map.title, visit.visitedAt, visit.visitId);
    return ele;
}

