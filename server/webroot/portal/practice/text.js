import {Component} from "./component.js";

export class Text extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(text){
        this.ele.html(text.content.replace(/\r\n|\n|\r/g, "<br/>\n"));
    }

}