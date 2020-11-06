import {Component} from "./component.js";
import {drugRep} from "../js/drug-util.js";
import {toZenkaku} from "../js/jp-util.js";

export class Drug extends Component {
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