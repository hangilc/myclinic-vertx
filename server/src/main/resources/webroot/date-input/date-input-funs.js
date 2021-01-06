import * as kanjidate from "../js/kanjidate.js";
import {success, failure} from "../js/result.js";
import {error} from "../portal-reception/js/opt-result.js";

export function setDate(map, sqldate){
    if( !sqldate ){
        clear(map);
        return;
    }
    let data = kanjidate.sqldatetimeToData(sqldate);
    setGengou(map, data.gengou.name);
    setNenInput(map, data.nen);
    setMonthInput(map, data.month);
    setDayInput(map, data.day);
}

export function clear(map){
    setNenInput(map, "");
    setMonthInput(map, "");
    setDayInput(map, "");
}

export function getDate(map, allowEmpty=false){
    if( isCleared(map) ){
        if( allowEmpty ){
            return success(null);
        } else {
            return error("入力されていません。");
        }
    }
    let gengou = getGengou(map);
    let nenInput = getNenInput(map);
    if( nenInput === "" ){
        return error("年が入力されていません。");
    }
    let nen = parseInt(nenInput);
    if( isNaN(nen) ){
        return error("年の入力が不適切です。");
    }
    let monthInput = getMonthInput(map);
    if( monthInput === "" ){
        return error("月が入力されていません。");
    }
    let month = parseInt(monthInput);
    if( isNaN(month) ){
        return error("月の入力が不適切です。");
    }
    let dayInput = getDayInput(map);
    if( dayInput === "" ){
        return error("日が入力されていません。");
    }
    let day = parseInt(dayInput);
    if( isNaN(day) ){
        return error("日の入力が不適切です。");
    }
    let year = kanjidate.gengouToSeireki(gengou, nen);
    if( kanjidate.isValidDate(year, month, day) ){
        return success(kanjidate.toSqldate(year, month, day));
    } else {
        return failure("不適切な日付です。");
    }
}

export function isCleared(map){
    return getNenInput(map) === "" && getMonthInput(map) === "" && getDayInput(map) === "";
}

export function setGengou(map, value){
    map.gengou.querySelector(`option[value='${value}']`).selected = true;
}

function getGengou(map){
    return map.gengou.value;
}

function setNenInput(map, value){
    map.nen.value = value;
}

function getNenInput(map){
    return map.nen.value;
}

function getMonthInput(map){
    return map.month.value;
}

function setMonthInput(map, value){
    map.month.value = value;
}

function getDayInput(map){
    return map.day.value;
}

function setDayInput(map, value){
    map.day.value = value;
}

export function advanceDays(map, n){
    let optDate = getDate(map);
    if( optDate.isSuccess() ){
        let date = optDate.getValue();
        let advanced = kanjidate.advanceDays(date, n);
        setDate(map, advanced);
    } else {
        alert(optDate.getMessage());
    }
}

export function advanceMonths(map, n){
    let optDate = getDate(map);
    if( optDate.isSuccess() ){
        let date = optDate.getValue();
        let advanced = kanjidate.advanceMonths(date, n);
        setDate(map, advanced);
    } else {
        alert(optDate.getMessage());
    }
}

export function advanceYears(map, n){
    let optDate = getDate(map);
    if( optDate.isSuccess() ){
        let date = optDate.getValue();
        let advanced = kanjidate.advanceYears(date, n);
        setDate(map, advanced);
    } else {
        alert(optDate.getMessage());
    }
}



