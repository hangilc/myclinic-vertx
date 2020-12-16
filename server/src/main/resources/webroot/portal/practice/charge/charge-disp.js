import {Component} from "../component.js";

export class ChargeDisp extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(charge){
        if( charge ){
            let value = +(charge.charge);
            this.ele.text(`請求額：${value.toLocaleString()}円`);
        } else {
            this.ele.text("［未請求］");
        }
    }
}