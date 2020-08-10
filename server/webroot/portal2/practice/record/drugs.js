import {drugRep} from "../../../portal/js/drug-util.js";
import {toZenkaku} from "../../../portal/js/jp-util.js";

export function populateDrugs(ele, drugFulls){
    if( drugFulls.length > 0 ){
        ele.append(createRp());
        for(let i=0;i<drugFulls.length;i++){
            let drugFull = drugFulls[i];
            ele.append(createDrugDisp(i+1, drugFull));
        }
    }
}

function createRp(){
    let e = document.createElement("div");
    e.innerText = "Ｒｐ）";
    return e;
}

function createDrugDisp(index, drugFull){
    let i = toZenkaku("" + index) + "）";
    let rep = drugRep(drugFull);
    let e = document.createElement("div");
    e.innerText = i + rep;
    return e;
}