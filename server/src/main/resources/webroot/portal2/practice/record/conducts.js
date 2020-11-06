import {createConduct} from "./conduct.js";


export function populateConducts(ele, conducts){
    addConducts(ele, conducts);
}

export function addConducts(ele, conducts){
    for(let cf of conducts){
        ele.append(createConduct(cf));
    }
}
