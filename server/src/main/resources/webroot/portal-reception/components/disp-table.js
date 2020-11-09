import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";

let tmpl = `
<div style="display:table"></div>
`;

let itemTmpl = `
    <div style="display:table-row">
        <div style="display:table-cell" class="mr-1 text-right">
            <spane class="x-key"></spane>：
        </div>
        <div style="display:table-cell" class="x-value"></div>
    </div>
`;

class Field {
    constructor(element, converter){
        this.element = element;
        this.converter = converter;
    }
}

export class DispTable {
    constructor(){
        this.ele = createElementFrom(tmpl);
        this.fields = [];
    }

    addField(key, conv){ // conv: data => string
        let item = createElementFrom(itemTmpl);
        let map = parseElement(item);
        map.key.innerText = key;
        this.ele.appendChild(item);
        let field = new Field(map.value, conv);
        this.fields.push(field);
    }

    set(data){
        for(let f of this.fields){
            f.element.innerText = f.converter(data);
        }
    }

    clear(){
        for(let f of this.fields){
            f.element.innerText = "";
        }
    }

    add(key, value){ // for backward compatibility
        this.addField(key, data => value);
    }
}