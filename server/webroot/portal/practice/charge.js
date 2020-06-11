import {Component} from "./component.js";

export class Charge extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(charge, chargeDispFactory){
        this.charge = charge;
        this.chargeDispFactory = chargeDispFactory;
        let compDisp = chargeDispFactory.create(charge);
        compDisp.appendTo(this.ele);
    }
}