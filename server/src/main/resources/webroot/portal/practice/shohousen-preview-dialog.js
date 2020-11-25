import {Dialog} from "./dialog.js";
import {PrintAPI} from "../../js/print-api.js";

export class ShohousenPreviewDialog extends Dialog {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.dispElement = map.disp;
        this.printElement = map.print;
        this.closeElement = map.close;
    }

    init(ops, drawerSvgModule){
        super.init();
        let svg = drawerSvgModule.drawerToSvg(ops,
            {width: "148mm", height: "210mm", viewBox: "0 0 148 210"});
        this.dispElement.html("");
        this.dispElement.append(svg);
        this.printElement.on("click", async event => {
            let printAPI = new PrintAPI("http://127.0.0.1:48080");
            await printAPI.print([], [ops], null);
            //await this.rest.printDrawer([ops]);
            this.hide();
        });
        this.closeElement.on("click", event => this.hide());
    }

    set(){
        super.set();
    }
}