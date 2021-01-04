import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {createDateInput} from "../../../date-input/date-input-base.js";

let tmpl = `
    <div>
        <div>
            名称：<span class="x-name"></span>
        </div>
        <div class="mt-1 x-date" style="font-size:12px">
            <select class="x-gengou">
                <option>令和</option>
            </select>
            <input type="text" size="1" class="x-nen" /> <span class="x-nen-label">年</span>
            <input type="text" size="1" class="x-month" /> <span class="x-month-label">月</span>
            <input type="text" size="1" class="x-day" /> <span class="x-day-label">日</span>
        </div>
        <div class="mt-1">
            <button type="button" class="x-enter btn btn-primary btn-sm">入力</button>
            <a href="javascript:void(0)" class="x-susp">の疑い</a>
            <a href="javascript:void(0)" class="x-del-adj">修飾語削除</a>
        </div>
        <form class="x-search-form mt-1">
            <div class="align-middle">
                <input type="text" class="x-search-text form-control border" size="7" />
                <button type="submit" class="btn btn-secondary btn-sm mt-1 border">検索</button>
                <a href="javascript:void(0)" class="x-example mt-1 ml-2 border">例</a>
            </div>
            <div class="mt-1" onsubmit="return false;">
                <input type="radio" name="search-kind" class="x-disease-radio" checked> 病名
                <input type="radio" name="search-kind" class="x-adj-radio"> 修飾語
            </div>
        </form>
        <div class="mt-1">
            <select size="10" class="x-select form-control"></select>
        </div>
    </div>
`;

export class Add {
    constructor() {
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.date = createDateInput(this.map.date, this.map);

    }
}