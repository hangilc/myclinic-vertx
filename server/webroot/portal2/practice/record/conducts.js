import {createConduct} from "./conduct.js";


export function populateConducts(ele, conducts){
    for(let conduct of conducts){
        let c = createConduct(conduct);
        ele.append(c);
    }
}
