import * as kanjidate from "../js/kanjidate.js";

export class DateInput {
    constructor(map){
        this.map = map;
        this.gengouElement = map.gengou;
        this.nenElement = map.nen;
        this.monthElement = map.month;
        this.dayElement = map.day;
        this.error = null;
    }

    init(){
        return this;
    }

    set(sqldate){
        if( sqldate ){
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