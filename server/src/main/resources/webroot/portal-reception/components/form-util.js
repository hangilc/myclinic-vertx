import * as kanjidate from "../js/kanjidate.js";

export function validFromRep(sqldate){
    return kanjidate.sqldateToKanji(sqldate);
}

export function validUptoRep(sqldate){
    if( !sqldate || sqldate === "0000-00-00" ){
        return "（期限なし）"
    } else {
        return kanjidate.sqldateToKanji(sqldate);
    }
}