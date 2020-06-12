import * as kanjidate from "../js/kanjidate.js";

export function isPrefix(shuushokugocode){
    return shuushokugocode < 8000;
}

export const shuushokugocodeSusp = 8002;

export function diseaseRep(diseaseFull){
    let pre = "";
    let post = "";
    for(let adj of diseaseFull.adjList){
        let code = adj.master.shuushokugocode;
        if( isPrefix(code) ){
            pre += adj.master.name;
        } else {
            post += adj.master.name;
        }
    }
    return pre + diseaseFull.master.name + post;
}

export function formatDate(sqldate){
    let data = kanjidate.sqldateToData(sqldate);
    return `${data.gengou.alpha[0]}${data.nen}.${data.month}.${data.day}`;
}

