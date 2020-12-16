import {Component} from "../component.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {ChargeDisp} from "./charge-disp.js";
import {click, on} from "../../../js/dom-helper.js";
import {ChargeModify} from "./charge-modify.js";

let tmpl = `
<div class="mt-2"></div>
`;

export class Charge {
    constructor(rest, charge){
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.addDisp(charge);
    }

    addDisp(charge){
        let disp = new ChargeDisp(charge);
        this.ele.append(disp.ele);
        if( charge ){
            click(disp.ele, async event => {
                let meisai = await this.rest.getMeisai(charge.visitId);
                let modify = new ChargeModify(this.rest, meisai, charge);
                on(modify.ele, "closed", event => {
                    let result = event.detail;
                    if( result ){
                        this.addDisp(result);
                    } else {
                        this.ele.append(disp.ele);
                    }
                });
                this.ele.innerHTML = "";
                this.ele.append(modify.ele);
            });
        }
    }
}

// export class ChargeOrig extends Component {
//     constructor(ele, map, rest) {
//         super(ele, map, rest);
//     }
//
//     init(chargeDispFactory, chargeModifyFactory){
//         this.chargeDispFactory = chargeDispFactory;
//         this.chargeModifyFactory = chargeModifyFactory;
//     }
//
//     set(charge){
//         this.charge = charge;
//         let compDisp = this.createDisp();
//         compDisp.appendTo(this.ele);
//     }
//
//     createDisp(){
//         let charge = this.charge;
//         let compDisp = this.chargeDispFactory.create(charge);
//         if( charge ) {
//             compDisp.ele.on("click", event => this.doModify(compDisp));
//         }
//         return compDisp;
//     }
//
//     async doModify(compDisp){
//         if( this.charge ){
//             let charge = this.charge;
//             let meisai = await this.rest.getMeisai(charge.visitId);
//             let compModify = this.chargeModifyFactory.create(meisai, charge);
//             compModify.onClose(result => {
//                 if( result ){
//                     compModify.remove();
//                     this.set(result);
//                 } else {
//                     compDisp.replace(compModify);
//                 }
//             });
//             compModify.replace(compDisp);
//         }
//     }
// }