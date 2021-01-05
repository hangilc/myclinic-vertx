import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {DateInputBase} from "./date-input-base.js";

export class DateInput extends DateInputBase {
    constructor(ele, map) {
        super();
        if( typeof ele === "string" ){
            ele = createElementFrom(ele);
        }
        if( !map ){
            map = parseElement(ele);
        }
        this.init(ele, map);
    }
}

let tmplRegular = `
    <div class="form-inline">
        <select class="x-gengou form-control mr-1">
            <option value="令和">令和</option>
            <option value="平成">平成</option>
            <option value="昭和" selected>昭和</option>
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

export class RegularDateInput extends DateInput {
    constructor() {
        super(tmplRegular);
    }
}

let tmplSmall = `
    <div class="form-inline">
        <select class="x-gengou form-control form-control-sm mr-1">
            <option value="令和">令和</option>
            <option value="平成">平成</option>
            <option value="昭和" selected>昭和</option>
            <option value="大正">大正</option>
            <option value="明治">明治</option>
        </select>
        <input type="text" class="x-nen form-control form-control-sm mx-1" style="width:3em"/>
        <span class="x-nen-label">年</span>
        <input type="text" class="x-month form-control form-control-sm mx-1" style="width:3em"/>
        <span class="x-month-label">月</span>
        <input type="text" class="x-day form-control form-control-sm mx-1" style="width:3em"/>
        <span class="x-day-label">日</span>
   </div>
`;

export class SmallDateInput extends DateInput {
    constructor(){
        super(tmplSmall);
    }
}

class DateInputOrig extends DateInputBase {
    constructor(opt = null) {
        super();
        if (!opt) {
            opt = {};
        }
        const ele = createElementFrom(tmpl);
        const map = parseElement(ele);
        if (opt.small) {
            [map.gengou, map.nen, map.month, map.day]
                .forEach(e => e.classList.add("form-control-sm"));
        }
        this.init(ele, map);
    }
}
