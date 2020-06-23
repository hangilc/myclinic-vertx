import {Component} from "./component.js";

export class Record extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.titleElement = map.title;
        this.textWrapperElement = map.textWrapper;
        this.drugWrapperElement = map.drugWrapper;
        this.drugPrepElement = map.drugPrep;
        this.conductWrapperElement = map.conductWrapper;
    }

    init(titleFactory, textFactory, drugFactory) {
        super.init();
        this.titleFactory = titleFactory;
        this.textFactory = textFactory;
        this.drugFactory = drugFactory;
        return this;
    }

    set(visitFull){
        super.set();
        let compTitle = this.titleFactory.create(visitFull.visit);
        compTitle.appendTo(this.titleElement);
        for(let text of visitFull.texts){
            let compText = this.textFactory.create(text);
            compText.appendTo(this.textWrapperElement);
        }
        if( visitFull.drugs.length > 0 ){
            this.drugPrepElement.removeClass("d-none");
        }
        let drugIndex = 1;
        for(let drug of visitFull.drugs){
            let compDrug = this.drugFactory.create(drugIndex++, drug);
            compDrug.appendTo(this.drugWrapperElement);
        }
        return this;
    }
}
