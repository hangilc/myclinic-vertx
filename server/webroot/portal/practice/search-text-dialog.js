import {Dialog} from "./dialog.js";
import {Component} from "./component.js";
import {parseElement} from "../js/parse-element.js";
import {TitleDisp} from "./title-disp.js";
import {TextDisp} from "./text-disp.js";
import {Nav} from "./nav.js";

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
        this.nav = new Nav(map.nav_, map.nav, rest);
        console.log("nav", map.nav);
        this.resultElement = map.result;
        this.currentPage = 0;
    }

    init(){
        this.searchFormElement.on("submit", event => {
            let promise = this.doSearch(1);
            return false;
        });
        this.nav.init();
        this.nav.onChange(async (event, page) => {
            console.log("nav", page);
            await this.doSearch(page);
        });
        this.ele.on("shown.bs.modal", event => this.searchTextElement.focus());
    }

    set(patientId){
        this.patientId = patientId;
        this.nav.set(0, 0);
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

    async doSearch(page){
        let text = this.searchTextElement.val();
        let result = await this.rest.searchText(this.patientId, text, page - 1);
        this.update(result);
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