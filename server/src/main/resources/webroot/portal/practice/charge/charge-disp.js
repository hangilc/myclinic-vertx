import {Component} from "../component.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";

let tmpl = `
    <div>
        <span class="x-charge-value"></span>
        <span class="x-payment-state"></span>
        <span class="text-danger x-no-pay-0410"></span>
    </div>
`;

export class ChargeDisp{

    constructor(charge){
        this.charge = charge;
        this.ele = createElementFrom(tmpl);
        this.ele.classList.add("charge-disp");
        this.map = parseElement(this.ele);
        if( charge ){
            let value = +(charge.charge);
            this.map.chargeValue.innerText = `請求額：${value.toLocaleString()}円`;
        } else {
            this.map.chargeValue.innerText = "［未請求］";
        }
        this.ele.addEventListener("update-payment", event => {
            this.updatePaymentState(event.detail)
        });
        this.ele.addEventListener("update-0410-no-pay", event => {
            this.update0410NoPay(event.detail)
        });
    }

    updatePaymentState(payment){
        if( this.charge ){
            if( payment ){
                let chargeValue = +this.charge.charge;
                let paymentValue = +payment.amount;
                if( chargeValue === paymentValue ){
                    this.setPaymentState("済");
                } else if( paymentValue === 0 ){
                    this.setPaymentState("未収", "text-danger");
                } else if( paymentValue < chargeValue ){
                    this.setPaymentState("領収不足", "text-warning");
                } else {
                    this.setPaymentState("領収超過", "text-warning");
                }
            }
        }
    }

    update0410NoPay(){
        this.map.noPay0410.innerText = "遠隔未収候補";
    }

    setPaymentState(text, color){
        let e = this.map.paymentState;
        e.classList.remove("text-light", "text-danger", "text-warning");
        e.innerText = `（${text}）`;
        if( color ) {
            e.classList.add(color);
        }
    }

}
