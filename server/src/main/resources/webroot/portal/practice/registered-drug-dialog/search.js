import {Component} from "../component2.js";
import {parseElement} from "../../js/parse-element.js";
import {drugExampleRep} from "../../../js/drug-util.js";

let template = `
    <div>
        <form class="form-inline x-form">
            <input type="text" class="form-control x-input"/>
            <button type="submit" class="btn btn-secondary ml-2">検索</button>
        </form>
        <select class="x-select form-control mt-2" size="6"></select>
    </div>
`;

let itemTemplate = `
    <option></option>
`;

export class Search extends Component {
    constructor(rest){
        super($(template));
        this.rest = rest;
        let map = parseElement(this.ele);
        map.form.on("submit", async event => {
            event.preventDefault();
            await this.doSearch();
        });
        this.inputElement = map.input;
        this.selectElement = map.select;
        this.selectElement.on("change", event => this.doSelected());
    }

    focus(){
        this.inputElement.focus();
    }

    async doSearch(){
        let text = this.inputElement.val().trim();
        if( text ){
            let result = await this.rest.searchPrescExample(text);
            this.setResult(result);
        }
    }

    setResult(drugExampleFulls){
        this.selectElement.html("");
        for(let full of drugExampleFulls){
            let rep = drugExampleRep(full);
            let opt = $(itemTemplate).data("data", full);
            opt.text(rep);
            this.selectElement.append(opt);
        }
    }

    onSelected(cb){
        this.on("selected", data => cb(data));
    }

    doSelected(){
        let data = this.selectElement.find("option:selected").data("data");
        if( data ){
            this.trigger("selected", data);
        }
    }

}