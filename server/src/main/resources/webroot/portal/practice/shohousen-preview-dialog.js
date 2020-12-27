import {Dialog} from "../../js/dialog2.js";
import {parseElement} from "../../js/parse-node.js";

let footerTmpl = `
    <button type="button" class="btn btn-primary x-print">印刷</button>
    <button type="button" class="btn btn-secondary x-close">閉じる</button>
`;

export class ShohousenPreviewDialog extends Dialog {
    constructor(prop){
        super();
        this.prop = prop;
        this.ops = [];
        this.ele.style.maxHeight = (window.innerHeight - 60) + "px";
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        fmap.print.addEventListener("click", async event => await this.doPrint());
        fmap.close.addEventListener("click", event => this.close(false));
    }

    init(ops, drawerSvgModule){
        this.ops = ops;
        let svg = drawerSvgModule.drawerToSvg(ops,
            {width: "148mm", height: "210mm", viewBox: "0 0 148 210"});
        this.getBody().append(svg);
    }

    set(){

    }

    async doPrint(){
        await this.prop.printAPI.print([], [this.ops], null);
        this.close(true);
    }

}




// import {Dialog} from "./dialog.js";
// import {PrintAPI} from "../../js/print-api.js";
//
// export class ShohousenPreviewDialog extends Dialog {
//     constructor(ele, map, rest, printAPI) {
//         super(ele, map, rest);
//         this.printAPI = printAPI;
//         this.dispElement = map.disp;
//         this.printElement = map.print;
//         this.closeElement = map.close;
//     }
//
//     init(ops, drawerSvgModule){
//         super.init();
//         let svg = drawerSvgModule.drawerToSvg(ops,
//             {width: "148mm", height: "210mm", viewBox: "0 0 148 210"});
//         this.dispElement.html("");
//         this.dispElement.append(svg);
//         this.printElement.on("click", async event => {
//             await this.printAPI.print([], [ops], null);
//             this.hide();
//         });
//         this.closeElement.on("click", event => this.hide());
//     }
//
//     set(){
//         super.set();
//     }
// }