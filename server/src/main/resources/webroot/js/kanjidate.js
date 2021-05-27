import {convertJsDate} from "../portal-reception/js/kanjidate.js";

const gengouList = [
    {
        name: "令和",
        start: "2019-05-01",
        year: 2019,
        alpha: "Reiwa"
    },
    {
        name: "平成",
        start: "1989-01-08",
        year: 1989,
        alpha: "Heisei"
    },
    {
        name: "昭和",
        start: "1926-12-25",
        year: 1926,
        alpha: "Shouwa"
    },
    {
        name: "大正",
        start: "1912-07-30",
        year: 1912,
        alpha: "Taishou"
    },
    {
        name: "明治",
        start: "1873-01-01",
        year: 1873,
        alpha: "Meiji"
    }
];

export function toSqldate(year, month, day){
    let y = ("" + year).padStart(4, "0");
    let m = ("" + month).padStart(2, "0");
    let d = ("" + day).padStart(2, "0");
    return `${y}-${m}-${d}`;
}

export function toSqldatetime(year, month, day, hours, minutes, seconds){
    let h = ("" + hours).padStart(2, "0");
    let m = ("" + minutes).padStart(2, "0");
    let s= ("" + seconds).padStart(2, "0");
    return toSqldate(year, month, day) + " " + `${h}:${m}:${s}`;
}

export function todayAsSqldate(){
    let today = new Date();
    return toSqldate(today.getFullYear(), today.getMonth() + 1, today.getDate());
}

export function parseSqldate(sqldate){
    let year = +sqldate.slice(0, 4);
    let month = +sqldate.slice(5, 7);
    let day = +sqldate.slice(8, 10);
    return {year, month, day};
}

export function parseSqltime(sqltime){
    let hour = +sqltime.slice(0, 2);
    let minute = +sqltime.slice(3, 5);
    let second = +sqltime.slice(6, 8);
    return {hour, minute, second};
}

export function seirekiToGengouData(year, month, day){
    let sqldate = toSqldate(year, month, day);
    for(let g of gengouList){
        if( sqldate >= g.start ){
            return [g, year - g.year + 1];
        }
    }
}

export function seirekiToGengou(year, month, day){
    let sqldate = toSqldate(year, month, day);
    for(let g of gengouList){
        if( sqldate >= g.start ){
            return [g.name, year - g.year + 1];
        }
    }
}

export function gengouToSeireki(gengou, nen){
    for(let g of gengouList){
        if( g.name === gengou ){
            return g.year + nen - 1;
        }
    }
    return null;
}

export function sqldateToKanji(sqldate, opts){
    if( !opts ){
        opts = {};
    }
    if( "zeroValue" in opts && sqldate === "0000-00-00" ){
        return opts.zeroValue;
    }
    let data = sqldateToData(sqldate);
    let gengou = data.gengou.name;
    let nen = "" + data.nen;
    let month = "" + data.month;
    let day = "" + data.day;
    if( opts.padZero ){
        nen = nen.padStart(2, "0");
        month = month.padStart(2, "0");
        day = day.padStart(2, "0");
    }
    let youbiStr = "";
    if( opts.youbi ){
        youbiStr = `（${data.youbi}）`;
    }
    return `${gengou}${nen}年${month}月${day}日${youbiStr}`;
}

export function sqldatetimeToKanji(sqldatetime, opts){
    if( !opts ){
        opts = {};
    }
    let datePart = sqldateToKanji(sqldatetime.substring(0, 10), opts);
    let data = sqldatetimeToData(sqldatetime);
    let hour = "" + data.hour;
    let minute = "" + data.minute;
    let second = "" + data.second;
    if( opts.padZero ){
        hour = hour.padStart(2, "0");
        minute = hour.padStart(2, "0");
        second = hour.padStart(2, "0");
    }
    let timePart = `${hour}時${minute}分${second}秒`;
    if( opts.omitSecond ){
        timePart = `${hour}時${minute}分`;
    }
    let sep = opts.sep || "";
    return datePart + sep + timePart;
}

let dayOfWeek = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
let youbi = ["日", "月", "火", "水", "木", "金", "土"];

export function sqldateToData(sqldate){
    let d = parseSqldate(sqldate);
    let [g, n] = seirekiToGengouData(d.year, d.month, d.day);
    let date = new Date(d.year, d.month - 1, d.day);
    let dayOfWeekIndex = date.getDay();
    return {
        gengou: g,
        nen: n,
        year: d.year,
        month: d.month,
        day: d.day,
        dayOfWeekIndex: dayOfWeekIndex,
        dayOfWeek: dayOfWeek[dayOfWeekIndex],
        youbi: youbi[dayOfWeekIndex],
        kanji: `${g.name}${n}年${d.month}月${d.day}日`,
        sqldate: sqldate
    };
}

export function nowAsSqldatetime(){
    let dt = new Date();
    return toSqldatetime(dt.getFullYear(), dt.getMonth() + 1, dt.getDate(),
        dt.getHours(), dt.getMinutes(), dt.getSeconds());
}

export function sqldatetimeToData(sqldatetime){
    let data = sqldateToData(sqldatetime.substring(0, 10));
    Object.assign(data, parseSqltime(sqldatetime.substring(11)));
    data.kanji += ` ${data.hour}時${data.minute}分${data.second}秒`
    return data;
}

export function calcAge(birthday){
    let bd = parseSqldate(birthday);
    let today = convertJsDate(new Date());
    let age = today.year - bd.year;
    if( today.month > bd.month ){
        return age;
    } else if( today.month < bd.month ){
        return age - 1;
    } else {
        if( today.day >= bd.day ){
            return age;
        } else {
            return age - 1;
        }
    }
}

export function lastDayOfMonth(year, month){
    return (new Date(year, month, 0).getDate());
}

export function toEndOfMonth(sqldate) {
    const {year, month} = parseSqldate(sqldate);
    return toSqldate(year, month, lastDayOfMonth(year, month));
}

export function endOfLastMonth(){
    return toEndOfMonth(advanceMonths(todayAsSqldate(), -1));
}

function padZero2(num){
    return ("" + num).padStart(2, "0");
}

export function getTimestamp(){
    let data = sqldatetimeToData(nowAsSqldatetime());
    let year = "" + data.year;
    let month = padZero2(data.month);
    let day = padZero2(data.day);
    let hour = padZero2(data.hour);
    let minute = padZero2(data.minute);
    let second = padZero2(data.second);
    return `${year}${month}${day}${hour}${minute}${second}`;
}

export function isValidDate(year, month, day){
    let d = new Date(year, month - 1, day);
    return d.getFullYear() === year && d.getMonth() === (month - 1) && d.getDate() === day;
}

export let fullGengouList = gengouList.map(g => g.name);
export let recentGengouList = [fullGengouList[0], fullGengouList[1]];
export let currentGengou = fullGengouList[0];

export function jsdateToSqldate(jsdate){
    return toSqldate(jsdate.getFullYear(), jsdate.getMonth() + 1, jsdate.getDate());
}

export function sqldateToJsdate(sqldate){
    const {year, month, day} = parseSqldate(sqldate);
    return new Date(year, month - 1, day);
}

export function youbiIndexOfSqldate(sqldate){
    return sqldateToJsdate(sqldate).getDay();
}

export function advanceDays(sqldate, n=1){
    let {year, month, day} = parseSqldate(sqldate);
    let d = new Date(year, month - 1, day + n);
    return jsdateToSqldate(d);
}

export function advanceMonths(sqldate, n=1){
    const {year: startYear, month: startMonth, day: startDay} = parseSqldate(sqldate);
    console.log(startYear, startMonth, startDay);
    const firstDay = new Date(startYear, startMonth - 1 + n, 1);
    console.log(firstDay);
    const year = firstDay.getFullYear();
    const month = firstDay.getMonth() + 1;
    const lastDay = lastDayOfMonth(year, month);
    const day = startDay > lastDay ? lastDay : startDay;
    return toSqldate(year, month, day);
}

export function advanceYears(sqldate, n=1){
    const {year: startYear, month: startMonth, day: startDay} = parseSqldate(sqldate);
    const firstDay = new Date(startYear + n, startMonth - 1, 1);
    const year = firstDay.getFullYear();
    const month = firstDay.getMonth() + 1;
    const lastDay = lastDayOfMonth(year, month);
    const day = startDay > lastDay ? lastDay : startDay;
    return toSqldate(year, month, day);
}

export function startOfWeek(sqldate, offset=0){
    const youbiIndex = youbiIndexOfSqldate(sqldate);
    return advanceDays(sqldate, -youbiIndex + offset);
}

export function consecutiveDays(startSqldate, n){
    const days = [];
    for(let i = 0; i<n;i++){
        days.push(advanceDays(startSqldate, i));
    }
    return days;
}
