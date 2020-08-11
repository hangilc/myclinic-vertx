import {parseElement} from "../../js/parse-element.js";
import {populateTitle} from "./title.js";
import {createText} from "./text.js";
import {populateTextCommands} from "./text-commands.js";
import {populateHoken} from "./hoken.js";
import {populateDrugs} from "./drugs.js";
import {populateShinryouCommands} from "./shinryou-commands.js";
import {populateShinryouList} from "./shinryou-list.js";
import {populateConductCommands} from "./conduct-commands.js";
import {populateConducts} from "./conducts.js";

let html = `
<div class="x-title title"></div>
<div class="x-left left">
    <div class="x-texts"></div>
    <div class="x-text-commands"></div>
</div>
<div class="x-right right">
    <div class="x-hoken"></div>
    <div class="x-drugs"></div>
    <div class="x-shinryou-commands"></div>
    <div class="x-shinryou-list"></div>
    <div class="x-conduct-commands"></div>
    <div class="x-conducts"></div>
</div>
`;

export function createRecord(visitFull, rest){
    let visit = visitFull.visit;
    let ele = document.createElement("div");
    ele.classList.add("record");
    ele.innerHTML = html;
    let map = parseElement(ele);
    populateTitle(map.title, visit.visitedAt, visit.visitId);
    map.texts.innerHTML = "";
    for(let text of visitFull.texts){
        let t = createText(text, rest);
        map.texts.append(t);
    }
    populateTextCommands(map.textCommands);
    populateHoken(map.hoken, visitFull.hoken);
    populateDrugs(map.drugs, visitFull.drugs);
    populateShinryouCommands(map.shinryouCommands);
    populateShinryouList(map.shinryouList, visitFull.shinryouList);
    populateConductCommands(map.conductCommands);
    populateConducts(map.conducts, visitFull.conducts);
    return ele;
}

