
export function populateShinryouList(ele, shinryouList){
    for(let i=0;i<shinryouList.length;i++){
        let sf = shinryouList[i];
        let e = createShinryouDisp(sf);
        ele.append(e);
    }
}

function createShinryouDisp(shinryouFull){
    let e = document.createElement("div");
    e.innerText = shinryouFull.master.name;
    return e;
}