import * as DiseaseUtil from "../../js/disease-util.js";

export function createDiseaseCurrent(diseaseFulls){
    let ele = document.createElement("div");
    ele.classList.add("disease-current");
    diseaseFulls.forEach(df => ele.append(createItem(df)));
    return ele;
}

function createItem(diseaseFull){
    let e = document.createElement("div");
    e.innerText = DiseaseUtil.diseaseFullRep(diseaseFull);
    return e;
}