import * as kanjidate from "../js/kanjidate.js";
import {parseElement} from "../js/parse-element.js";

let html = `
    <select class="x-gengou form-control">
        <option>令和</option>
        <option>平成</option>
        <option selected>昭和</option>
        <option>大正</option>
        <option>明治</option>
    </select>
    <input type="text" class="x-nen form-control ml-2" size="3"/> 年
    <input type="text" class="x-month form-control ml-2" size="3"/> 月
    <input type="text" class="x-day form-control ml-2" size="3"/> 日
`;

export class DateInput {
    constructor(ele){
        if( !ele ){
            ele = $("<div>");
        } else {
            ele = $(ele);
        }
        if( ele.children().length === 0 ){
            ele.html(html);
        }
        this.ele = ele;
        this.map = parseElement(ele);
        this.gengouElement = this.map.gengou;
        this.nenElement = this.map.nen;
        this.monthElement = this.map.month;
        this.dayElement = this.map.day;
        this.error = null;
    }

    init(){
        return this;
    }

    set(sqldate){
        if( sqldate && sqldate !== "0000-00-00" ){
            let data = kanjidate.sqldatetimeToData(sqldate);
            this.gengouElement.val(data.gengou.name);
            this.nenElement.val(data.nen);
            this.monthElement.val(data.month);
            this.dayElement.val(data.day);
        } else {
            this.nenElement.val("");
            this.monthElement.val("");
            this.dayElement.val("");
        }
        return this;
    }

    allowEmpty(emptyValue="0000-00-00"){
        this.allowEmpty = true;
        this.emptyValue = emptyValue;
        return this;
    }

    get(){
        if( this.isEmpty() ){
            if( this.allowEmpty ){
                return this.emptyValue;
            } else {
                this.error = "入力されていません。";
                return undefined;
            }
        }
        let gengou = this.gengouElement.val();
        if( this.nenElement.val() === "" ){
            this.error = "年が入力されていません。";
            return undefined;
        }
        let nen = parseInt(this.nenElement.val());
        if( isNaN(nen) ){
            this.error ="年の入力が不適切です。";
            return undefined;
        }
        if( this.monthElement.val() === "" ){
            this.error = "月が入力されていません。";
            return undefined;
        }
        let month = parseInt(this.monthElement.val());
        if( isNaN(nen) ){
            this.error ="月の入力が不適切です。";
            return undefined;
        }
        if( this.dayElement.val() === "" ){
            this.error = "日が入力されていません。";
            return undefined;
        }
        let day = parseInt(this.dayElement.val());
        if( isNaN(nen) ){
            this.error ="日の入力が不適切です。";
            return undefined;
        }
        let year = kanjidate.gengouToSeireki(gengou, nen);
        return kanjidate.toSqldate(year, month, day);
    }

    isEmpty(){
        return this.nenElement.val() === "" && this.monthElement.val() === "" &&
            this.dayElement.val() === "";
    }

    getError(){
        return this.error;
    }

    val(value){
        if( arguments.length >= 1 ){
            this.set(value);
        } else {
            return this.get();
        }
    }
}