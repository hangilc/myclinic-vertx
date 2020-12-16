import {Widget} from "../widget.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {click} from "../../../js/dom-helper.js";

let tmpl = `
    <div class="mb-3 border border-secondary rounded p-2">
        <div class="d-flex p-2 mb-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">請求額の変更</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div>
            <div class="row">
                <div class="col-sm-5 d-flex justify-content-end">診療報酬総点</div>
                <div class="col-sm-7"><span class="x-total-ten mr-1"></span>点</div>
                <div class="col-sm-5 d-flex justify-content-end">負担割</div>
                <div class="col-sm-7"><span class="x-futan-wari mr-1"></span>割</div>
                <div class="col-sm-5 d-flex justify-content-end">現在の請求額</div>
                <div class="col-sm-7"><span class="x-current-charge mr-1"></span>円</div>
            </div>
            <div class="form-group row">
                <div class="col-sm-5 col-form-label d-flex justify-content-end">変更後請求額</div>
                <div class="col-sm-7 form-inline">
                    <input type="text" class="form-control x-charge mr-1" size="6"/>円
                </div>
            </div>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button class="btn btn-link x-receipt-pdf">領収書PDF</button>
            <button class="btn btn-secondary x-enter">入力</button>
            <button class="btn btn-secondary x-cancel ml-2">キャンセル</button>
        </div>
    </div>
`;

let formTmpl = `
    <form class="d-none" method="POST" target="_blank">
        <input type="text" name="paper" value="A6_Landscape"/>
        <input type="text" name="pages" />
        <input type="text" name="stamp" value="receipt"/>
    </form>
`;

export class ChargeModify {

    constructor(rest, meisai, charge, visit){
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.setCharge(meisai, charge);
        click(this.map.enter, async event => {
            let value = parseInt(this.map.charge.value);
            if( isNaN(value) ){
                alert("請求額の入力が不適切です。");
                return;
            }
            if( !charge ){
                alert("Charge is not available.");
                return;
            }
            let visitId = charge.visitId;
            await rest.modifyCharge(visitId, value);
            let updated = await rest.getCharge(visitId);
            this.close(updated);
        });
        click(this.map.receiptPdf, async event => {
            let req = await createReceiptDrawerReq(rest, meisai, charge.charge, visit);
            let ops = await rest.receiptDrawer(req);
            let url = rest.urlViewDrawerAsPdf();
            let form = createElementFrom(formTmpl);
            form.action = url;
            form.querySelector("input[name='pages'").value = JSON.stringify([ops]);
            document.body.append(form);
            form.submit();
            form.remove();
            this.close(null);
        });
        click(this.map.widgetClose, event => this.close(null));
        click(this.map.cancel, event => this.close(null));
    }

    setCharge(meisai, charge){
        this.map.totalTen.innerText = meisai.totalTen.toLocaleString();
        this.map.futanWari.innerText = meisai.futanWari;
        this.map.currentCharge.innerText = charge.charge.toLocaleString();
        this.map.charge.value = meisai.charge;
    }

    close(value){
        this.ele.dispatchEvent(new CustomEvent("closed", {detail: value}));
        this.ele.remove();
    }

}

async function createReceiptDrawerReq(rest, meisai, chargeValue, visit){
    return {
        meisai,
        patient: await  rest.getPatient(visit.patientId),
        visit,
        charge: chargeValue,
        clinicInfo: await rest.getClinicInfo()
    };
}

// export class ChargeModifyOrig extends Widget {
//     constructor(ele, map, rest){
//         super(ele, map, rest);
//         this.totalTenElement = map.totalTen;
//         this.futanWariElement = map.futanWari;
//         this.currentChargeElement = map.currentCharge;
//         this.chargeElement = map.charge;
//         this.enterElement = map.enter;
//         this.cancelElement = map.cancel;
//     }
//
//     init(){
//         super.init();
//         this.enterElement.on("click", event => this.doEnter());
//         this.cancelElement.on("click", event => this.close(null));
//         return this;
//     }
//
//     set(meisai, charge){
//         super.set();
//         this.charge = charge;
//         this.totalTenElement.text(meisai.totalTen.toLocaleString());
//         this.futanWariElement.text(meisai.futanWari);
//         this.currentChargeElement.text(charge.charge.toLocaleString());
//         this.chargeElement.val(meisai.charge);
//         return this;
//     }
//
//     async doEnter(){
//         let value = parseInt(this.chargeElement.val());
//         if( isNaN(value) ){
//             alert("請求額の入力が不適切です。");
//             return;
//         }
//         let charge = this.charge;
//         if( !charge ){
//             alert("Charge is not available.");
//             return;
//         }
//         let visitId = charge.visitId;
//         await this.rest.modifyCharge(visitId, value);
//         let updated = await this.rest.getCharge(visitId);
//         this.close(updated);
//     }
//
// }
