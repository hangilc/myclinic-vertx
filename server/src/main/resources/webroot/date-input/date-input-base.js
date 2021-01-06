import * as funs from "./date-input-funs.js";
import {click} from "../js/dom-helper.js";
import * as kanjidate from "../js/kanjidate.js";

export function createDateInput(ele, map){
    const dateInput = new DateInputBase();
    dateInput.init(ele, map);
    return dateInput;
}

export class DateInputBase {
    init(ele, map) {
        this.ele = ele;
        this.map = map;
        this.enableLabelClick();
    }

    set(sqldate){
        funs.setDate(this.map, sqldate);
    }

    setToday(){
        this.set(kanjidate.todayAsSqldate());
    }

    clear(){
        funs.clear(this.map);
    }

    isCleared(){
        return funs.isCleared(this.map);
    }

    get(){
        return funs.getDate(this.map);
    }

    setGengouList(list){
        let select = this.map.gengou;
        select.innerHTML = "";
        list.forEach(g => {
            let opt = document.createElement("option");
            opt.innerText = g;
            opt.value = g;
            select.append(opt);
        });
    }

    setGengou(gengou){
        funs.setGengou(this.map, gengou);
    }

    enableLabelClick(){
        if( this.map.nenLabel ){
            this.map.nenLabel.style.cursor = "pointer";
            click(this.map.nenLabel, event => this.doNenClick(event));
        }
        if( this.map.monthLabel ){
            this.map.monthLabel.style.cursor = "pointer";
            click(this.map.monthLabel, event => this.doMonthClick(event));
        }
        if( this.map.dayLabel ){
            this.map.dayLabel.style.cursor = "pointer";
            click(this.map.dayLabel, event => this.doDayClick(event));
        }
    }

    doNenClick(event){
        let n = 1;
        if( event.shiftKey ){
            n = -n;
        }
        funs.advanceYears(this.map, n);
    }

    doMonthClick(event){
        let n = 1;
        if( event.shiftKey ){
            n = -n;
        }
        funs.advanceMonths(this.map, n);
    }

    doDayClick(event){
        let n = 1;
        if( event.ctrlKey ){
            n = 7;
        }
        if( event.shiftKey ){
            n = -n;
        }
        funs.advanceDays(this.map, n);
    }
}