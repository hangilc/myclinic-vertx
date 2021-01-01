import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import * as funs from "./date-input-funs.js";

let tmpl = `
    <div class="form-inline">
        <select class="x-gengou form-control mr-1">
            <option value="令和">令和</option>
            <option value="平成">平成</option>
            <option  value="昭和" selected>昭和</option>
            <option value="大正">大正</option>
            <option value="明治">明治</option>
        </select>
        <input type="text" class="x-nen form-control mx-1" style="width:3em"/>
        <span class="x-nen-label">年</span>
        <input type="text" class="x-month form-control mx-1" style="width:3em"/>
        <span class="x-month-label">月</span>
        <input type="text" class="x-day form-control mx-1" style="width:3em"/>
        <span class="x-day-label">日</span>
   </div>
`;

export class DateInput {
    constructor(opt=null) {
        if( !opt ){
            opt = {};
        }
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        if( opt.small ){
            [this.map.gengou, this.map.nen, this.map.month, this.map.day]
                .forEach(e => e.classList.add("form-control-sm"));
        }
    }

    set(sqldate){
        funs.setDate(this.map, sqldate);
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
}