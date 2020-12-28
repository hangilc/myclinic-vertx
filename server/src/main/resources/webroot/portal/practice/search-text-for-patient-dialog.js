import {SearchTextDialog} from "./search-text-dialog.js";
import {parseElement} from "../js/parse-element.js";
import {Component} from "../js/component.js";
import {TitleDisp} from "./title/title-disp.js";
import {TextDisp} from "./text/text-disp.js";

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

export class SearchTextForPatientDialog extends SearchTextDialog {
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