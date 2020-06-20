import {Component} from "./component.js";

export class ChargeModify extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.totalTenElement = map.totalTen;
        this.futanWariElement = map.futanWari;
        this.currentChargeElement = map.currentCharge;
        this.chargeElement = map.charge;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
    }

    init(){
        super.init();
        return this;
    }

    set(meisai, charge){
        console.log("meisai", meisai);
        super.set();
        this.totalTenElement.text(meisai.totalTen.toLocaleString());
        this.futanWariElement.text(meisai.futanWari);
        this.currentChargeElement.text(charge.charge.toLocaleString());
        this.chargeElement.val(meisai.charge);
        return this;
    }
}
