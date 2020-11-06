import {Component} from "../../js/component.js";
import {parseElement} from "../../js/parse-element.js";
import {toZenkaku} from "../../js/jp-util.js";
import {drugRep} from "../../js/drug-util.js";

let template = `
    <div><span class="x-index"></span>）<span class="x-rep"></span></div>
`;

class Drug extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.indexElement = map.index;
        this.repElement = map.rep;
    }

    init(){

    }

    set(index, drugFull){
        this.setIndex(index);
        this.setDrug(drugFull);
    }

    setIndex(index){
        this.indexElement.text(toZenkaku(index));
    }

    setDrug(drugFull){
        this.repElement.text(drugRep(drugFull));
    }

}

class DrugFactory {
    create(index, drugFull, rest){
        let ele = $(template);
        let map = parseElement(ele);
        let comp = new Drug(ele, map, rest);
        comp.init();
        comp.set(index, drugFull);
        return comp;
    }
}

export let drugFactory = new DrugFactory();
