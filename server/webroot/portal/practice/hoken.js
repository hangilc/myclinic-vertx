import {Component} from "./component.js";

export class Hoken extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(hokenRep){
        this.ele.text(hokenRep);
    }
}