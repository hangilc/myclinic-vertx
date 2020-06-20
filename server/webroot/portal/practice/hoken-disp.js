import {Component} from "./component.js";

export class HokenDisp extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
    }

    init(){
        super.init();
        return this;
    }

    set(hokenRep){
        super.set();
        this.ele.text(hokenRep);
        return this;
    }
}