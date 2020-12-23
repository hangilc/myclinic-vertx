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
    constructor(rest, charge, visit){
        this.visit = visit;
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.addDisp(charge, visit);
    }

    addDisp(charge){
        let disp = new ChargeDisp(charge);
        this.ele.append(disp.ele);
        if( charge ){
            click(disp.ele, async event => {
                let meisai = await this.rest.getMeisai(charge.visitId);
                let modify = new ChargeModify(this.rest, meisai, charge, this.visit);
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

    updatePaymentState(payment){
        let chargeEle = this.ele.querySelector(".charge-disp");
        if( chargeEle ) {
            chargeEle.dispatchEvent(new CustomEvent("update-payment", {detail: payment}));
        }
    }
}
