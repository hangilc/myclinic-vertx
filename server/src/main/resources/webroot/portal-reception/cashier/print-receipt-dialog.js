import {Dialog} from "../components/dialog.js";
import {DrawerDisp} from "../components/drawer-disp.js";

export class PrintReceiptDialog extends Dialog {
    constructor(ops){
        super();
        this.setTitle("領収書印刷");
        let w = 297;
        let h = 210;
        let scale = 2;
        let disp = new DrawerDisp(ops, `${w * scale}`, `${h * scale}`, `0, 0, ${w}, ${h}`);
        this.getBody().appendChild(disp.ele);
    }

}