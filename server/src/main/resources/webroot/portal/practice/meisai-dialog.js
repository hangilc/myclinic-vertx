import {Dialog} from "./dialog.js"
import {MeisaiDetail} from "./meisai-detail.js";

export class MeisaiDialog extends Dialog {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.detailElement = map.detail_;
        this.detailMap = map.detail;
        this.modifyChargeButton = this.detailMap.modifyChargeButton;
        this.modifyChargeWorkarea = this.detailMap.modifyChargeWorkarea;
        this.modifyChargeInput = this.detailMap.modifyChargeInput;
        this.modifyChargeEnter = this.detailMap.modifyChargeEnter;
        this.modifyChargeCancel = this.detailMap.modifyChargeCancel;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
        this.setDialogResult(false);
    }

    init(meisai){
        super.init();
        let meisaiDetail = new MeisaiDetail(this.detailElement, this.detailMap, this.rest);
        meisaiDetail.init(meisai);
        this.enterElement.on("click", event => {
            this.setDialogResult(true);
            this.hide();
        });
        this.cancelElement.on("click", event => {
            this.setDialogResult(false);
            this.hide();
        });
        this.modifyChargeButton.on("click", event => {
            this.modifyChargeWorkarea.removeClass("d-none");
        });
        this.modifyChargeEnter.on("click", event => {
            let v = parseInt(this.modifyChargeInput.val().trim());
            if( isNaN(v) ){
                alert("請求額の入力が不適切です。");
            } else {
                meisai.charge = v;
                meisaiDetail.updateChargeValue(v);
                this.modifyChargeWorkarea.addClass("d-none");
            }
        });
        this.modifyChargeCancel.on("click", event => {
            this.modifyChargeWorkarea.addClass("d-none");
        });
    }

    set(){
        super.set();
    }

}