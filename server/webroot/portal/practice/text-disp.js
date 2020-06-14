import {Component} from "./component.js";

export class TextDisp extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(){

    }

    set(text){
        this.ele.html(text.content.replace(/\r\n|\n|\r/g, "<br/>\n"));
    }
}