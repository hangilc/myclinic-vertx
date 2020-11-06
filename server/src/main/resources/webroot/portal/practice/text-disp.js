import {Component} from "./component.js";

export class TextDisp extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(){

    }

    set(text){
        let content = text.content;
        if( content.startsWith("院外処方") ){
            content = content.replace(/　/g, " "); // replace zenkaku space to ascii space
        }
        this.ele.html(content.replace(/\r\n|\n|\r/g, "<br/>\n"));
    }
}