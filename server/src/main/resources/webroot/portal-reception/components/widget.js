import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";

let tmpl = `
<div class="card">
    <div class="card-header x-title"></div>
    <div class="card-body x-body"></div>
    <div class="card-body x-commands text-right pt-0"></div>
</div>
`;

export class Widget {
    constructor(title){
        this.ele = createElementFrom(tmpl);
        let map = parseElement(this.ele);
        map.title.innerText = title;
        this.map = map;
    }

    getContent(){
        return this.map.body;
    }

    getCommands(){
        return this.map.commands;
    }

    close(){
        this.ele.remove();
    }
}