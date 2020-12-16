import {Component} from "../component.js";

export class Charge extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(chargeDispFactory, chargeModifyFactory){
        this.chargeDispFactory = chargeDispFactory;
        this.chargeModifyFactory = chargeModifyFactory;
    }

    set(charge){
        this.charge = charge;
        let compDisp = this.createDisp();
        compDisp.appendTo(this.ele);
    }

    createDisp(){
        let charge = this.charge;
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
            compModify.onClose(result => {
                if( result ){
                    compModify.remove();
                    this.set(result);
                } else {
                    compDisp.replace(compModify);
                }
            });
            compModify.replace(compDisp);
        }
    }
}