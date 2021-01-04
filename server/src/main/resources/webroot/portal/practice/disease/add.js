import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";

let tmpl = `
    <div>
        <div>
            名称：<span class="x-name"></span>
        </div>
        <div class="mt-1">
            <input type="date" class="x-date-input form-control"/>
        </div>
        <div class="mt-1">
            <button type="button" class="x-enter btn btn-secondary">入力</button>
            <a href="javascript:void(0)" class="x-susp">の疑い</a>
            <a href="javascript:void(0)" class="x-del-adj">修飾語削除</a>
        </div>
        <form class="x-search-form mt-1">
            <div class="form-inline">
                <input type="text" class="x-search-text form-control"/>
                <button type="submit" class="btn btn-secondary mt-1">検索</button>
                <a href="javascript:void(0)" class="x-example mt-1 ml-2">例</a>
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
    }
}