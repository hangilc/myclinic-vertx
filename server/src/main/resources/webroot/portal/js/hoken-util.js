
export function hokenToHokenList(hoken){
    let result = {};
    result.shahokokuhoList = hoken.shahokokuho ? [hoken.shahokokuho] : [];
    result.koukikoureiList = hoken.koukikourei ? [hoken.koukikourei] : [];
    result.roujinList = hoken.roujin ? [hoken.roujin] : [];
    result.kouhiList = [];
    if( hoken.kouhi1 ){
        result.kouhiList.push(hoken.kouhi1);
    }
    if( hoken.kouhi2 ){
        result.kouhiList.push(hoken.kouhi2);
    }
    if( hoken.kouhi3 ){
        result.kouhiList.push(hoken.kouhi3);
    }
    return result;
}