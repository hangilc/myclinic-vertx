import * as DiseaseUtil from "../../js/disease-util.js";
import * as F from "../functions.js";

export function createDiseaseCurrent(diseaseFulls){
    let ele = document.createElement("div");
    ele.classList.add("disease-current");
    diseaseFulls.forEach(df => ele.append(createItem(df)));
    return ele;
}

function createItem(diseaseFull){
    let e = document.createElement("div");
    e.classList.add("disp");
    e.innerText = DiseaseUtil.diseaseFullRep(diseaseFull);
    e.onclick = event => e.dispatchEvent(F.event("disease-clicked", diseaseFull));
    return e;
}