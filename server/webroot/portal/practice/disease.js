import {Component} from "./component.js";

export class Disease extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(diseaseFull, diseaseDispFactory){
        let disp = diseaseDispFactory.create(diseaseFull);
        disp.appendTo(this.ele);
    }

}