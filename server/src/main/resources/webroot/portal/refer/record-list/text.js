import {Component} from "../../js/component.js";
import {parseElement} from "../../js/parse-element.js";

let template = `
    <div></div>
`;

class Text extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(){
        super.init();
    }

    set(text){
        super.set();
        this.ele.html(text.content.replace(/\r\n|\n|\r/g, "<br/>\n"));
    }
}

class TextFactory {
    create(text, rest){
        let ele = $(template);
        let map = parseElement(ele);
        let comp = new Text(ele, map, rest);
        comp.init();
        comp.set(text);
        return comp;
    }
}

export let textFactory = new TextFactory();
