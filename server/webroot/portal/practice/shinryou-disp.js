import {Component} from "./component.js";

export class ShinryouDisp extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(shinryouFull){
        this.ele.text(shinryouFull.master.name);
    }
}