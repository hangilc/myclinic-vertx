import * as kanjidate from "../js/kanjidate.js";

export function isPrefix(shuushokugocode){
    return shuushokugocode < 8000;
}

export const shuushokugocodeSusp = 8002;

export function diseaseRep(diseaseFull){
    return diseaseRepByMasters(diseaseFull.master, diseaseFull.adjList.map(adj => adj.master));
}

export function diseaseRepByMasters(byoumeiMaster, shuushokugoMasters){
    let pre = "";
    let post = "";
    for(let adj of shuushokugoMasters){
        let code = adj.shuushokugocode;
        if( isPrefix(code) ){
            pre += adj.name;
        } else {
            post += adjname;
        }
    }
    let name = byoumeiMaster ? byoumeiMaster.name : "";
    return pre + name + post;
}

export function formatDate(sqldate){
    let data = kanjidate.sqldateToData(sqldate);
    return `${data.gengou.alpha[0]}${data.nen}.${data.month}.${data.day}`;
}
