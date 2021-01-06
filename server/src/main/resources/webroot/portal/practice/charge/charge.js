import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {ChargeDisp} from "./charge-disp.js";
import {click, on} from "../../../js/dom-helper.js";
import {ChargeModify} from "./charge-modify.js";
import * as app from "../app.js";

let tmpl = `
<div class="mt-2 charge-listener payment-listener no-pay-0410-listener"></div>
`;

export class Charge {
    constructor(props){
        this.props = props;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.addDisp();
        this.ele.addEventListener("charge-changed", event => {
            this.props.charge = event.detail;
            this.updateUI();
        });
        this.ele.addEventListener("payment-available", event => {
            this.props.payment = event.detail;
            this.updateUI();
        });
        this.ele.addEventListener("payment-changed", event => {
            this.props.payment = event.detail;
            this.updateUI();
        });
        this.ele.addEventListener("no-pay-0410-updated", event => {
            this.props.isNoPay0410 = app.isNoPay0410(this.props.visitId);
            this.updateUI();
        });
    }

    getCharge(){
        return this.props.charge;
    }

    getPayment(){
        return this.props.payment;
    }

    isNoPay0410(){
        return this.props.isNoPay0410;
    }

    getVisitId(){
        return this.getCharge().visitId;
    }

    updateUI(){
        const evt = new Event("update-ui");
        this.ele.querySelectorAll(".charge-ui").forEach(e => e.dispatchEvent(evt));
    }

    addDisp(){
        let disp = new ChargeDisp(this.props);
        this.ele.append(disp.ele);
        let charge = this.getCharge();
        if( charge ){
            click(disp.ele, async event => {
                const visitId = this.getVisitId();
                let meisai = await app.rest.getMeisai(visitId);
                let payment = this.getPayment();
                let visit = await app.rest.getVisit(visitId);
                let modify = new ChargeModify(app.rest, meisai, charge, visit, payment);
                on(modify.ele, "closed", event => {
                    this.addDisp();
                });
                this.ele.innerHTML = "";
                this.ele.append(modify.ele);
            });
        }
    }

}
