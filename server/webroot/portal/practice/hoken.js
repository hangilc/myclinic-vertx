import {Component} from "./component.js";

export class Hoken extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(hokenDispFactory){
        this.hokenDispFactory = hokenDispFactory;
    }

    set(hoken, hokenRep){
        let compDisp = this.hokenDispFactory.create(hokenRep);
        this.ele.html("");
        compDisp.appendTo(this.ele);
    }
}