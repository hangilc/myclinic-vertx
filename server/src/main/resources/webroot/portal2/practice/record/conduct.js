import {parseElement} from "../../js/parse-element.js";
import * as consts from "../../../portal/js/consts.js";

let html = `
<div class="x-kind"></div>
<div class="x-gazou-label"></div>
<div class="x-shinryou"></div>
<div class="x-drug"></div>
<div class="x-kizai"></div>
`;

export function createConduct(conductFull){
    let ele = document.createElement("div");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.kind.innerText = consts.conductKindToKanji(conductFull.conduct.kind)
    map.gazouLabel.innerText = conductFull.gazouLabel.label || "";
    conductFull.conductShinryouList.forEach(cs => {
        map.shinryou.append(createElement(conductShinryouRep(cs)));
    });
    conductFull.conductDrugs.forEach(cd => {
        map.shinryou.append(createElement(conductDrugRep(cd)));
    });
    conductFull.conductKizaiList.forEach(ck => {
        map.shinryou.append(createElement(conductKizaiRep(ck)));
    });
    return ele;
}

function createElement(text){
    let e = document.createElement("div");
    e.innerText = text;
    return e;
}

function conductShinryouRep(conductShinryouFull){
    return conductShinryouFull.master.name;
}

function conductDrugRep(conductDrugFull){
    let name = conductDrugFull.master.name;
    let amount = conductDrugFull.conductDrug.amount;
    let unit = conductDrugFull.master.unit;
    return `${name} ${amount}${unit}`;
}

function conductKizaiRep(conductKizaiFull){
    let name = conductKizaiFull.master.name;
    let amount = conductKizaiFull.conductKizai.amount;
    let unit = conductKizaiFull.master.unit;
    return `${name} ${amount}${unit}`;
}
