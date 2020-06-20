import {Component} from "./component.js";

export class Charge extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(charge, chargeDispFactory, chargeModifyFactory){
        this.charge = charge;
        this.chargeDispFactory = chargeDispFactory;
        this.chargeModifyFactory = chargeModifyFactory;
        let compDisp = this.createDisp(charge);
        compDisp.appendTo(this.ele);
    }

    createDisp(charge){
        let compDisp = this.chargeDispFactory.create(charge);
        if( charge ) {
            compDisp.ele.on("click", event => this.doModify(compDisp));
        }
        return compDisp;
    }

    async doModify(compDisp){
        if( this.charge ){
            let charge = this.charge;
            let meisai = await this.rest.getMeisai(charge.visitId);
            let compModify = this.chargeModifyFactory.create(meisai, charge);
            compModify.replace(compDisp);
        }
    }
}