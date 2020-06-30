import {Component} from "../component.js";
import {parseElement} from "../../js/parse-element.js";

let template = `
    <div>
        <form class="form-inline x-form">
            <input type="text" class="form-control x-search-text"/>
            <button type="submit" class="btn btn-secondary ml-2">検索</button>
        </form>
        <select size="10" class="x-select form-control mt-2"></select>
    </div>
`;

let optionTemplate = `
    <option></option>
`;

export class ShinryouSearch extends Component {
    constructor(rest, ele, map){
        if( !ele ){
            ele = $(template);
        }
        if( !map ){
            map = parseElement(ele);
        }
        super(ele, map, rest);
        this.formElement = map.form;
        this.searchTextElement = map.searchText;
        this.selectElement = map.select;
    }

    init(visitedAt){
        super.init();
        if( visitedAt.length > 10 ){
            visitedAt = visitedAt.substring(0, 10);
        }
        this.visitedAt = visitedAt;
        this.formElement.on("submit", event => {
            this.doSearch();
            return false;
        });
        return this;
    }

    set(){
        super.set();
        return this;
    }

    getSelectedData(){
        return this.selectElement.find("option:selected").data("master");
    }

    setResult(result){
        this.selectElement.html("");
        for(let master of result){
            let opt = $(optionTemplate);
            opt.text(master.name);
            opt.data("master", master);
            this.selectElement.append(opt);
        }
    }

    async doSearch(){
        let text = this.searchTextElement.val().trim();
        if( text ){
            let result = await this.rest.searchShinryouMaster(text, this.visitedAt);
            this.setResult(result);
        }
    }

    focus(){
        this.searchTextElement.focus();
    }
}
