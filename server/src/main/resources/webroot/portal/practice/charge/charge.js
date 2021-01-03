import {Component} from "../component.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {ChargeDisp} from "./charge-disp.js";
import {click, on} from "../../../js/dom-helper.js";
import {ChargeModify} from "./charge-modify.js";
import * as app from "../app.js";

let tmpl = `
<div class="mt-2 payment-listener"></div>
`;

export class Charge {
    constructor(props){
        this.props = props;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.addDisp();
        this.ele.addEventListener("payment-updated", event => {
            const payment = event.detail;
            console.log(payment);
        });
    }

    addDisp(){
        let disp = new ChargeDisp(this.props);
        this.ele.append(disp.ele);
        let charge = this.props.charge;
        if( charge ){
            click(disp.ele, async event => {
                let meisai = await this.rest.getMeisai(charge.visitId);
                let payment = await this.rest.getLastPayment(this.visit.visitId);
                let modify = new ChargeModify(this.rest, meisai, charge, this.visit, payment);
                on(modify.ele, "closed", event => {
                    let result = event.detail;
                    if( result ){
                        this.addDisp(result);
                        this.firePaymentUpdated();
                    } else {
                        this.ele.append(disp.ele);
                    }
                });
                this.ele.innerHTML = "";
                this.ele.append(modify.ele);
            });
        }
    }

    firePaymentUpdated(){
        this.ele.dispatchEvent(new CustomEvent("payment-updated", {
            bubbles: true,
            detail: [this.visit.visitId]
        }))
    }

    updatePaymentState(payment){
        let chargeEle = this.ele.querySelector(".charge-disp");
        if( chargeEle ) {
            chargeEle.dispatchEvent(new CustomEvent("update-payment", {detail: payment}));
        }
    }

    update0410NoPay(){
        let chargeEle = this.ele.querySelector(".charge-disp");
        if( chargeEle ) {
            chargeEle.dispatchEvent(new Event("update-0410-no-pay"));
        }
    }
}
