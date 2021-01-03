import {Component} from "../component.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";

let tmpl = `
    <div class="charge-ui">
        <span class="x-charge-value"></span>
        <span class="x-payment-state"></span>
        <span class="text-danger x-no-pay-0410"></span>
    </div>
`;

export class ChargeDisp{

    constructor(props){
        this.props = props;
        this.ele = createElementFrom(tmpl);
        this.ele.classList.add("charge-disp");
        this.map = parseElement(this.ele);
        this.updateUI();
        this.ele.addEventListener("update-ui", event => {
            this.updateUI();
        });
    }

    updateUI(){
        this.updateChargeValueUI();
        this.updatePaymentStateUI();
        this.updateNoPay0410UI();
    }

    updateChargeValueUI(){
        let charge = this.props.charge;
        if( charge ){
            let value = +(charge.charge);
            this.map.chargeValue.innerText = `請求額：${value.toLocaleString()}円`;
        } else {
            this.map.chargeValue.innerText = "［未請求］";
        }
    }

    updatePaymentStateUI(){
        let charge = this.props.charge;
        let payment = this.props.payment;
        let wrapper = this.map.paymentState;
        wrapper.innerHTML = "";
        if( payment ){
            let chargeValue = +charge.charge;
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

    updateNoPay0410UI(){
        this.map.noPay0410.innerText = this.props.isNoPay0410 ? "遠隔未収候補" : "";
    }

    setPaymentState(text, color){
        let e = this.map.paymentState;
        e.innerHTML = "";
        e.append(createSpan(text, color));
    }

}

function createSpan(text, color){
    let e = document.createElement("span");
    e.innerText = text;
    e.classList.add(color);
    return e;
}
