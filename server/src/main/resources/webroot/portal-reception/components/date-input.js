import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import * as kanjidate from "../js/kanjidate.js";
import {success, error} from "../js/opt-result.js";

let tmpl = `
<div class="form-inline">
    <select class="x-gengou form-control mr-1">
        <option value="令和">令和</option>
        <option value="平成">平成</option>
        <option  value="昭和" selected>昭和</option>
        <option value="大正">大正</option>
        <option value="明治">明治</option>
    </select>
    <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/> 年
    <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
    <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
</div>
`;

export class DateInput {
    constructor(){
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.allowEmpty = false;
        this.emptyValue = "0000-00-00";
    }

    setGengouRecent(){
        this.initGengouSelect(["令和", "平成"]);
        this.setSelectedGengou("令和");
    }

    initGengouSelect(gengouList){
        this.map.gengou.querySelectorAll("option").forEach(opt => {
            if( gengouList.includes(opt.value) ){
                opt.classList.remove("d-none");
            } else {
                opt.classList.add("d-none");
            }
        });
    }

    setSelectedGengou(gengou){
        this.map.gengou.value = gengou;
    }

    setAllowEmpty(boolValue){
        this.allowEmpty = boolValue;
    }

    setEmptyValue(emptyValue){
        this.emptyValue = emptyValue;
    }

    getGengou(){
        return this.map.gengou.value;
    }

    setGengou(value){
        this.map.gengou.querySelector(`option[value='${value}']`).selected = true;
    }

    getNenInput(){
        return this.map.nen.value;
    }

    setNenInput(value){
        this.map.nen.value = value;
    }

    getMonthInput(){
        return this.map.month.value;
    }

    setMonthInput(value){
        this.map.month.value = value;
    }

    getDayInput(){
        return this.map.day.value;
    }

    setDayInput(value){
        this.map.day.value = value;
    }

    isEmpty(){
        return this.getNenInput() === "" && this.getMonthInput() === "" && this.getDayInput() === "";
    }

    set(sqldate){
        if( sqldate && sqldate !== "0000-00-00" ){
            let data = kanjidate.sqldatetimeToData(sqldate);
            this.setGengou(data.gengou.name);
            this.setNenInput(data.nen);
            this.setMonthInput(data.month);
            this.setDayInput(data.day);
        } else {
            this.setNenInput("");
            this.setMonthInput("");
            this.setDayInput("");
        }
    }

    get(){
        if( this.isEmpty() ){
            if( this.allowEmpty ){
                return success(this.emptyValue);
            } else {
                return error("入力されていません。");
            }
        }
        let gengou = this.getGengou();
        if( this.getNenInput() === "" ){
            return error("年が入力されていません。");
        }
        let nen = parseInt(this.getNenInput());
        if( isNaN(nen) ){
            return error("年の入力が不適切です。");
        }
        if( this.getMonthInput() === "" ){
            return error("月が入力されていません。");
        }
        let month = parseInt(this.getMonthInput());
        if( isNaN(nen) ){
            return error("月の入力が不適切です。");
        }
        if( this.getDayInput() === "" ){
            return error("日が入力されていません。");
        }
        let day = parseInt(this.getDayInput());
        if( isNaN(nen) ){
            return error("日の入力が不適切です。");
        }
        let year = kanjidate.gengouToSeireki(gengou, nen);
        return success(kanjidate.toSqldate(year, month, day));
    }

}