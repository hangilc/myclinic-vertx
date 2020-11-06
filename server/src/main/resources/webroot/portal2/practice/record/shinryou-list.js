import {createShinryou} from "./shinryou.js";
import * as F from "../functions.js";

export function populateShinryouList(ele, shinryouList, rest){
    shinryouList.forEach(sf => {
        let e = createShinryou(sf, rest);
        ele.append(e);
    });
}

export function addShinryouList(ele, shinryouList, rest){
    let srcList = Array.from(shinryouList);
    if( srcList.length === 0 ){
        return;
    }
    srcList.sort((a, b) => a.shinryou.shinryoucode - b.shinryou.shinryoucode);
    let curList = F.removeChildren(ele);
    while( curList.length > 0 || srcList.length > 0 ){
        let cur = curList[0];
        let src = srcList[0];
        if( isBefore(cur, src) ){
            ele.append(cur);
            curList.shift();
        } else {
            ele.append(createShinryou(src, rest));
            srcList.shift();
        }
    }
}

function isBefore(cur, src){
    if( cur ){
        if( src ){
            return cur.dataset.shinryoucode <= src.shinryou.shinryoucode;
        } else {
            return true;
        }
    } else {
        return false;
    }
}
