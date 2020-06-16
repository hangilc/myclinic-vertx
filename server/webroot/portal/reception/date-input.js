import * as kanjidate from "../js/kanjidate.js";

export class DateInput {
    constructor(map){
        this.map = map;
        this.gengouElement = map.gengou;
        this.nenElement = map.nen;
        this.monthElement = map.month;
        this.dayElement = map.day;
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

    get(){

    }

    val(value){
        if( arguments.length >= 1 ){
            this.set(value);
        } else {
            return this.get();
        }
    }
}