import {Dialog} from "./dialog.js"
import {MeisaiDetail} from "./meisai-detail.js";

export class MeisaiDialog extends Dialog {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.detailElement = map.detail_;
        this.detailMap = map.detail;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
        this.setDialogResult(false);
    }

    init(meisai){
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
    }

}