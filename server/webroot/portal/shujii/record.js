import {Component} from "./component.js";

export class Record extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.titleElement = map.title;
        this.textWrapperElement = map.textWrapper;
    }

    init(titleFactory, textFactory){
        super.init();
        this.titleFactory = titleFactory;
        this.textFactory = textFactory;
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
        return this;
    }
}
