
let youbiList = ["日", "月", "火", "水", "木", "金", "土"];

export function getYoubi(date){
    return youbiList[date.getDay()];
}

export function sqldatetimeToDate(sqldate){
    sqldate = sqldate.substring(0, 10) + "T" + sqldate.substring(11);
    return new Date(sqldate);
}