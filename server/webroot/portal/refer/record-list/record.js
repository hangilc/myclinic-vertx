import {Component} from "../component.js";
import {parseElement} from "../../js/parse-element.js";
import {titleFactory} from "./title.js";
import {textFactory} from "./text.js";
import {drugFactory} from "./drug.js";

let template = `
    <div class="mb-3">
        <div class="x-title font-weight-bold py-1 px-2" style="background-color: #eee"></div>
        <div class="x-text-wrapper"></div>
        <div class="x-drug-wrapper">
            <div class="d-none x-drug-prep">Rp）</div>
        </div>
        <div class="x-conduct-wrapper"></div>
    </div>
`;

class Record extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.titleElement = map.title;
        this.textWrapperElement = map.textWrapper;
        this.drugWrapperElement = map.drugWrapper;
        this.drugPrepElement = map.drugPrep;
        this.conductWrapperElement = map.conductWrapper;
    }

    init() {
        super.init();
        return this;
    }

    set(visitFull){
        super.set();
        let compTitle = titleFactory.create(visitFull.visit, this.rest);
        compTitle.appendTo(this.titleElement);
        for(let text of visitFull.texts){
            let compText = textFactory.create(text);
            compText.appendTo(this.textWrapperElement);
        }
        if( visitFull.drugs.length > 0 ){
            this.drugPrepElement.removeClass("d-none");
        }
        let drugIndex = 1;
        for(let drug of visitFull.drugs){
            let compDrug = drugFactory.create(drugIndex++, drug);
            compDrug.appendTo(this.drugWrapperElement);
        }
        return this;
    }
}

class RecordFactory {
    create(visitFull, rest){
        let ele = $(template);
        let map = parseElement(ele);
        let comp = new Record(ele, map, rest);
        comp.init();
        comp.set(visitFull);
        return comp;
    }
}

export let recordFactory = new RecordFactory();
