import {Widget} from "./widget2.js";
import {KouhiDisp} from "./kouhi-disp.js";
import * as kanjidate from "../js/kanjidate.js";

let commandsTmpl = `
    <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
`;

export class KouhiDispWidget extends Widget {
    constructor(kouhi){
        super();
        this.setTitle("公費負担データ");
        this.form = new KouhiDisp(this.getContentElement());
        this.form.set(kouhi);
        let cmap = this.setCommands(commandsTmpl);
        cmap.close.addEventListener("click", event => this.close());
    }

}
