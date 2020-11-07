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

export class DispTable {
    constructor(){
        this.ele = createElementFrom(tmpl);
    }

    add(key, value){
        let item = createElementFrom(itemTmpl);
        let map = parseElement(item);
        map.key.innerText = key;
        map.value.innerText = value;
        this.ele.appendChild(item);
    }
}