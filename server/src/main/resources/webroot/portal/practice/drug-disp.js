import {Component} from "./component.js";
import {drugRep} from "../../js/drug-util.js";
import {toZenkaku} from "../../js/jp-util.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";

let tmpl = `
    <div><span class="x-index"></span>）<span class="x-rep"></span></div>
`;

export class DrugDisp {
    constructor(drugFull, index=null) {
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.rep.innerText = drugRep(drugFull);
        if( index ){
            this.setIndex(index);
        }
    }

    setIndex(index){
        this.map.index.innerText = toZenkaku(index);
    }

}