import {parseElement} from "../../../js/parse-node.js";
import {DateInputBase} from "../../../date-input/date-input-base.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import * as base from "../../../date-input/date-input-base.js";
import * as kanjidate from "../../../js/kanjidate.js";

const tmpl = `
    <div class="form-inline" style="font-size:12px;">
        <select class="x-gengou mr-1 form-control form-control-sm" style="font-size:12px">
        </select>
        <input type="text" class="x-nen form-control form-control-sm mr-1" style="width:38px; "/> 
            <span class="x-nen-label mr-1">年</span>
        <input type="text" class="x-month form-control form-control-sm mr-1"  style="width:38px; "/> 
            <span class="x-month-label mr-1">月</span>
        <input type="text" class="x-day form-control form-control-sm mr-1"  style="width:38px; "/> 
            <span class="x-day-label">日</span>
    </div>
`;

export function createDateInput(){
    let ele = createElementFrom(tmpl);
    let map = parseElement(ele);
    const input = base.createDateInput(ele, map);
    input.setGengouList(kanjidate.recentGengouList);
    return input;
}