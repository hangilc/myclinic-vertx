import * as kanjidate from "../js/kanjidate.js";

export function setDate(map, sqldate) {
    if (!sqldate) {
        throw new Error("日付が設定されていません。");
    }
    let data = kanjidate.sqldatetimeToData(sqldate);
    setGengou(map, data.gengou.name);
    setNenInput(map, data.nen);
    setMonthInput(map, data.month);
    setDayInput(map, data.day);
}

export function clear(map) {
    setNenInput(map, "");
    setMonthInput(map, "");
    setDayInput(map, "");
}

export function getDate(map, errs) {
    if (isCleared(map)) {
        throw new Error("入力がありません。");
    }
    let gengou = getGengou(map);
    let nenInput = getNenInput(map);
    if (nenInput === "") {
        throw new Error("年が入力されていません。");
    }
    let nen = parseInt(nenInput);
    if (isNaN(nen)) {
        throw new Error("年の入力が不適切です。");
    }
    let monthInput = getMonthInput(map);
    if (monthInput === "") {
        throw new Error("月が入力されていません。");
    }
    let month = parseInt(monthInput);
    if (isNaN(month)) {
        throw new Error("月の入力が不適切です。");
    }
    let dayInput = getDayInput(map);
    if (dayInput === "") {
        throw new Error("日が入力されていません。");
    }
    let day = parseInt(dayInput);
    if (isNaN(day)) {
        throw new Error("日の入力が不適切です。");
    }
    let year = kanjidate.gengouToSeireki(gengou, nen);
    if (!kanjidate.isValidDate(year, month, day)) {
        throw new Error("不適切な日付です。");
    }
    return kanjidate.toSqldate(year, month, day);
}

export function isCleared(map) {
    return getNenInput(map) === "" && getMonthInput(map) === "" && getDayInput(map) === "";
}

export function setGengou(map, value) {
    map.gengou.querySelector(`option[value='${value}']`).selected = true;
}

function getGengou(map) {
    return map.gengou.value;
}

function setNenInput(map, value) {
    map.nen.value = value;
}

function getNenInput(map) {
    return map.nen.value;
}

function getMonthInput(map) {
    return map.month.value;
}

function setMonthInput(map, value) {
    map.month.value = value;
}

function getDayInput(map) {
    return map.day.value;
}

function setDayInput(map, value) {
    map.day.value = value;
}

export function advanceDays(map, n) {
    let date = getDate(map);
    let advanced = kanjidate.advanceDays(date, n);
    setDate(map, advanced);
}

export function advanceMonths(map, n) {
    let date = getDate(map);
    let advanced = kanjidate.advanceMonths(date, n);
    setDate(map, advanced);
}

export function advanceYears(map, n) {
    let date = getDate(map);
    let advanced = kanjidate.advanceYears(date, n);
    setDate(map, advanced);
}



