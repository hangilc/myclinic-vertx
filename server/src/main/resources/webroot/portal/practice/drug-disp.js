import {Component} from "./component.js";
import {drugRep} from "../js/drug-util.js";
import {toZenkaku} from "../js/jp-util.js";

export class DrugDisp extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.indexElement = map.index;
        this.repElement = map.rep;
    }

    init(drugFull){
        this.repElement.text(drugRep(drugFull));
    }

    setIndex(index){
        this.indexElement.text(toZenkaku(index));
    }

}