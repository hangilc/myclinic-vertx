import {SearchTextDialog} from "./search-text-dialog.js";
import {parseElement} from "../../js/parse-node.js";
import {Component} from "../js/component.js";
import {TitleDisp} from "./title-disp.js";
import {TextDisp} from "./text/text-disp.js";
import {Dialog} from "../../js/dialog2.js";
import {click, submit} from "../../js/dom-helper.js";
import {titleRep} from "./title/title-funs.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {textRep} from "./text/text-funs.js";

let tmpl = `
    <form class="x-search-form form-inline" onsubmit="return false;">
        <input type="text" class="x-search-text form-control"/>
        <button type="submit" class="btn btn-primary ml-2">検索</button>
    </form>
    <div class="x-nav d-none mt-2"></div>
    <div class="x-result" style="width:400px;"></div>
`;

export class SearchTextForPatientDialog extends Dialog {
    constructor(prop) {
        super();
        this.prop = prop;
        this.rest = rest;
        this.setTitle("文章検索");
        this.getBody().innerHTML = tmpl;
        let map = this.map = parseElement(this.getBody());
        submit(map.searchForm, async event => await this.doSearch());
    }

    initFocus(){
        this.map.searchText.focus();
    }

    async doSearch(){
        let patient = this.prop.patient;
        if( !patient ){
            alert("Cannot find current specified");
            return;
        }
        let patientId = patient.patientId;
        let text = this.map.searchText.value.trim();
        if( text === "" ){
            return;
        }
        let result = await this.rest.searchText(patientId, text, 0);
        console.log(result);
        let page = result.page;
        let totalPages = result.totalPages;
        let items = result.textVisits.map(result => new Item(result.visit, result.text));
        this.setItems(items);
    }

    setItems(items){
        let wrapper = this.map.result;
        wrapper.innerHTML = "";
        items.forEach(item => wrapper.append(item.ele));
    }
}

let itemTmpl = `
    <div class="my-2 border border-secondary rounded p-2">
        <div class="bg-light font-weight-bold p-1">
            <div class="x-title-text"></div>
        </div>
        <div class="x-text mt-2"></div>
    </div>
`;

class Item {
    constructor(visit, text) {
        this.ele = createElementFrom(itemTmpl);
        let map = parseElement(this.ele);
        map.titleText.innerText = titleRep(visit.visitedAt);
        map.text.innerHTML = textRep(text.content);
    }
}

class SearchTextForPatientDialogOrig extends SearchTextDialog {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.itemTemplate = map.itemTemplate;
    }

    init(dialogTitle){
        super.init(dialogTitle);
        this.itemTemplateHtml = this.itemTemplate.html();
        this.onSearch((text, page) => this.doSearch(text, page));
    }

    set(patientId){
        super.set();
        this.patientId = patientId;
    }

    update(searchTextResult){
        let page = searchTextResult.page;
        let totalPages = searchTextResult.totalPages;
        let textVisits = searchTextResult.textVisits;
        this.nav.set(page + 1, totalPages);
        this.resultElement.html("");
        for(let textVisit of textVisits){
            let compItem = this.createItem(textVisit);
            compItem.appendTo(this.resultElement);
        }
    }

    async doSearch(text, page){
        let result = await this.rest.searchText(this.patientId, text, page - 1);
        this.update(result);
    }

    createItem(textVisit){
        let ele = $(this.itemTemplateHtml);
        let map = parseElement(ele);
        let compItem = new Item(ele, map, this.rest);
        compItem.init();
        compItem.set(textVisit);
        return compItem;
    }
}

