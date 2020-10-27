import {Widget} from "./widget2.js";
import {RoujinDisp} from "./roujin-disp.js";
import * as kanjidate from "../js/kanjidate.js";

let commandsTmpl = `
    <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
`;

export class RoujinDispWidget extends Widget {
    constructor(roujin){
        super();
        this.setTitle("老人保険データ");
        this.form = new RoujinDisp(this.getContentElement());
        this.form.set(roujin);
        let cmap = this.setCommands(commandsTmpl);
        cmap.close.addEventListener("click", event => this.close());
    }

}
