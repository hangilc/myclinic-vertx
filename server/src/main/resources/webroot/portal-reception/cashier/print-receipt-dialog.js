import {DrawerDisp} from "../components/drawer-disp.js";
import {PrintDialog} from "../components/print-dialog.js";

let paper = { // A6 landscape
    w: 148,
    h: 105
};

let previewScale = 3;

let previewSpec = {
    width: paper.w * previewScale,
    height: paper.h * previewScale,
    viewBox: `0, 0, ${paper.w}, ${paper.h}`
}

// export class PrintReceiptDialog extends PrintDialog {
//     constructor(ops) {
//         super([], [ops], previewSpec);
//         this.setTitle("領収書印刷");
//     }
//
// }

export async function openPrintReceiptDialog(ops){
    let dialog = new PrintDialog([], [ops], previewSpec);
    dialog.setTitle("領収書印刷");
    await dialog.initSetting("receipt");
    return dialog.open();
}