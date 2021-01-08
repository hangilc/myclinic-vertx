import {parseElement} from "../../js/parse-node.js";
import {Dialog} from "../../js/dialog2.js";
import {submit} from "../../js/dom-helper.js";
import {titleRep} from "./title/title-funs.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {textRep} from "../../js/text-util.js";

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


