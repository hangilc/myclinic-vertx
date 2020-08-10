
let youbiList = ["日", "月", "火", "水", "木", "金", "土"];

export function getYoubi(date){
    return youbiList[date.getDay()];
}