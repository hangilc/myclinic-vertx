import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {createDateInput} from "../../../date-input/date-input-base.js";
import {click, on, submit} from "../../../js/dom-helper.js";
import * as app from "../app.js";
import * as DiseaseUtil from "../../js/disease-util.js";

let examples = [];

export function initExamples(examples_){
    examples = examples_;
}

let tmpl = `
    <div>
        <div>
            名称：<span class="x-name"></span>
        </div>
        <div class="mt-1 form-inline x-date_" style="font-size:12px;">
            <select class="x-gengou mr-1 form-control form-control-sm" style="font-size:12px">
                <option value="令和">令和</option>
            </select>
            <input type="text" class="x-nen form-control form-control-sm mr-1" style="width:38px; "/> 
                <span class="x-nen-label mr-1">年</span>
            <input type="text" class="x-month form-control form-control-sm mr-1"  style="width:38px; "/> 
                <span class="x-month-label mr-1">月</span>
            <input type="text" class="x-day form-control form-control-sm mr-1"  style="width:38px; "/> 
                <span class="x-day-label">日</span>
        </div>
        <div class="mt-1 mb-2 d-flex align-items-center">
            <button type="button" class="x-enter btn btn-primary btn-sm mr-2">入力</button>
            <a href="javascript:void(0)" class="mr-2 x-susp">の疑い</a>
            <a href="javascript:void(0)" class="x-del-adj">修飾語削除</a>
        </div>
        <form class="x-search-form mt-1" onsubmit="return false;">
            <div class="form-inline">
                <input type="text" class="x-search-text form-control mr-2" style="width:60px; flex-grow: 1"/>
                <button type="submit" class="x-search btn btn-secondary btn-sm mr-2">検索</button>
                <a href="javascript:void(0)" class="x-example">例</a>
            </div>
            <div class="mt-1" onsubmit="return false;">
                <input type="radio" name="search-kind" class="x-disease-radio" value="byoumei" checked> 病名
                <input type="radio" name="search-kind" class="x-adj-radio" value="adj"> 修飾語
            </div>
        </form>
        <div class="mt-1">
            <select size="10" class="x-select form-control"></select>
        </div>
    </div>
`;

export class Add {
    constructor() {
        this.props = {
            master: null,
            adjList: []
        };
        this.ele = createElementFrom(tmpl);
        let map = this.map = parseElement(this.ele);
        this.dateInput = createDateInput(this.map.date_, this.map.date);
        this.dateInput.setToday();
        submit(map.searchForm, async event => await this.doSearch());
        on(map.select, "change", event => {
            let opt = map.select.querySelector("option:checked");
            console.log(opt);
            if( opt ){
                if( opt.dataset.kind === "byoumei" ){
                    this.props.master = opt.master;
                } else if( opt.dataset.kind === "adj" ){
                    this.props.adjList.push(opt.master);
                }
                this.updateName();
            }
        });
    }

    initFocus(){
        this.map.searchText.focus();
    }

    updateName(){
        this.map.name.innerText = rep(this.props.master, this.props.adjList);
    }

    async doSearch(){
        const text = this.map.searchText.value.trim();
        if( !text ){
            return;
        }
        const dateOpt = this.dateInput.get();
        if( dateOpt.isFailure() ){
            alert(dateOpt.getMessage());
            return;
        }
        const date = dateOpt.getValue();
        const searchKind = this.getSearchKind();
        const select = this.map.select;
        select.innerHTML = "";
        let masters = [];
        if( searchKind === "byoumei" ){
            masters = await app.rest.searchByoumeiMaster(text, date);
        } else if( searchKind === "adj" ){
            masters = await app.rest.searchShuushokugoMaster(text, date);
        }
        masters.forEach(master => select.append(createOption(master, searchKind)));

    }

    getSearchKind(){
        return this.map.searchForm.querySelector("input[name='search-kind']:checked").value;
    }
}

function createOption(master, kind){
    const opt = document.createElement("option");
    opt.innerText = master.name;
    opt.master = master;
    opt.dataset.kind = kind;
    return opt;
}

function rep(master, adjList){
    return DiseaseUtil.diseaseRepByMasters(master, adjList);
}
