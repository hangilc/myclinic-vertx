import {Dialog} from "./dialog.js";
import {Component} from "./component.js";
import {parseElement} from "../js/parse-element.js";
import {TitleDisp} from "./title-disp.js";
import {TextDisp} from "./text-disp.js";

class Item extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.title = new TitleDisp(map.title_, map.title, rest);
        this.text = new TextDisp(map.text_, map.text, rest);
    }

    init(){
        this.title.init();
        this.text.init();
    }

    set(textVisit){
        this.title.set(textVisit.visit.visitedAt);
        this.text.set(textVisit.text);
    }
}

export class SearchTextDialog extends Dialog {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.searchFormElement = map.searchForm;
        this.searchTextElement = map.searchText;
        this.itemTemplateHtml = map.itemTemplate.html();
        this.resultElement = map.result;
        this.currentPage = 0;
    }

    init(){
        this.searchFormElement.on("submit", event => { this.doSearch(); return false; });
        this.ele.on("shown.bs.modal", event => this.searchTextElement.focus());
    }

    set(patientId){
        this.patientId = patientId;
    }

    async doSearch(){
        let text = this.searchTextElement.val();
        let result = await this.rest.searchText(this.patientId, text, 0);
        let page = result.page;
        let totalPages = result.totalPages;
        let textVisits = result.textVisits;
        this.resultElement.html("");
        for(let textVisit of textVisits){
            let compItem = this.createItem(textVisit);
            compItem.appendTo(this.resultElement);
        }
    }

    createItem(textVisit){
        let ele = $(this.itemTemplateHtml);
        let map = parseElement(ele);
        let compItem = new Item(ele, map, this.rest);
        compItem.init(this.textFactory);
        compItem.set(textVisit);
        return compItem;
    }

}