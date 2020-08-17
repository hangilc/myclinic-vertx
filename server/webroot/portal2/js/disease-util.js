
export function isPrefix(shuushokugocode){
    return shuushokugocode < 8000;
}

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
            post += adj.name;
        }
    }
    let name = byoumeiMaster ? byoumeiMaster.name : "";
    return pre + name + post;
}

export function diseaseFullRep(diseaseFull, sep=" "){
    return diseaseRep(diseaseFull) + sep + datePart(diseaseFull.disease);
}

export function formatDate(sqldate){
    return sqldate;
}

export function datePart(disease){
    let start = formatDate(disease.startDate);
    if( disease.endReason !== "N" ){
        let end = formatDate(disease.endDate);
        return `(${start}-${end})`;
    } else {
        return `(${start})`;
    }
}

