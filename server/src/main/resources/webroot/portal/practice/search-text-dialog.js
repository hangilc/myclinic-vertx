import {Dialog} from "./dialog.js";
import {NavGeneric} from "./nav-generic.js";

export class SearchTextDialog extends Dialog {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.titleElement = map.title;
        this.searchFormElement = map.searchForm;
        this.searchTextElement = map.searchText;
        this.nav = new NavGeneric(map.nav_, rest);
        this.resultElement = map.result;
    }

    init(dialogTitle){
        super.init();
        this.titleElement.text(dialogTitle);
        this.searchFormElement.on("submit", event => {
            this.trigger("search", 1);
            return false;
        });
        this.nav.setCallback(page => this.trigger("search", page));
        this.ele.on("shown.bs.modal", event => this.searchTextElement.focus());
    }

    set(){
        super.set();
        this.nav.set(0, 0);
    }

    onSearch(cb){
        this.on("search", (event, page) => {
            let text = this.searchTextElement.val();
            cb(text, page);
        });
    }

}