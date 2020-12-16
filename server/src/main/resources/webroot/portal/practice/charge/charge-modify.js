import {Widget} from "../widget.js";

export class ChargeModify extends Widget {
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
        this.enterElement.on("click", event => this.doEnter());
        this.cancelElement.on("click", event => this.close(null));
        return this;
    }

    set(meisai, charge){
        super.set();
        this.charge = charge;
        this.totalTenElement.text(meisai.totalTen.toLocaleString());
        this.futanWariElement.text(meisai.futanWari);
        this.currentChargeElement.text(charge.charge.toLocaleString());
        this.chargeElement.val(meisai.charge);
        return this;
    }

    async doEnter(){
        let value = parseInt(this.chargeElement.val());
        if( isNaN(value) ){
            alert("請求額の入力が不適切です。");
            return;
        }
        let charge = this.charge;
        if( !charge ){
            alert("Charge is not available.");
            return;
        }
        let visitId = charge.visitId;
        await this.rest.modifyCharge(visitId, value);
        let updated = await this.rest.getCharge(visitId);
        this.close(updated);
    }

}
