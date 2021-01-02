import * as kanjidate from "../../js/kanjidate.js";

export function titleRep(sqldatetime) {
    let data = kanjidate.sqldatetimeToData(sqldatetime);
    let nen = (data.nen + "").padStart(2, "0");
    let month = (data.month + "").padStart(2, "0");
    let day = (data.day + "").padStart(2, "0");
    let hour = (data.hour + "").padStart(2, "0");
    let minute = (data.minute + "").padStart(2, "0");
    return `${data.gengou.name}${nen}年${month}月${day}日（${data.youbi}） ${hour}時${minute}分`;
}

