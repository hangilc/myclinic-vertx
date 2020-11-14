import {Dialog} from "../components/dialog.js";
import {DrawerDisp} from "../components/drawer-disp.js";



export class PrintReceiptDialog extends Dialog {
    constructor(ops){
        super();
        this.setTitle("領収書印刷");
        let w = 148;
        let h = 105;
        let scale = 3;
        let disp = new DrawerDisp(ops, `${w * scale}`, `${h * scale}`, `0, 0, ${w}, ${h}`);
        this.getBody().appendChild(disp.ele);
    }

}