import {Dialog} from "./dialog.js";
import {Nav} from "./nav.js";

export class SearchTextDialog extends Dialog {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.titleElement = map.title;
        this.searchFormElement = map.searchForm;
        this.searchTextElement = map.searchText;
        this.nav = new Nav(map.nav_, map.nav, rest);
        this.resultElement = map.result;
    }

    init(dialogTitle){
        this.titleElement.text(dialogTitle);
        this.searchFormElement.on("submit", event => {
            this.trigger("search", 1);
            return false;
        });
        this.nav.init();
        this.nav.onChange((event, page) => {
            this.trigger("search", page);
        });
        this.ele.on("shown.bs.modal", event => this.searchTextElement.focus());
    }

    set(){
        this.nav.set(0, 0);
    }

    onSearch(cb){
        this.on("search", (event, page) => {
            let text = this.searchTextElement.val();
            cb(text, page);
        });
    }

}