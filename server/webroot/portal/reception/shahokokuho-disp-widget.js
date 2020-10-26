import {Widget} from "./widget2.js";
import {ShahokokuhoDisp} from "./shahokokuho-disp.js";

let commandsTmpl = `
    <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
`;

export class ShahokokuhoDispWidget extends Widget {
    constructor(shahokokuho) {
        super();
        this.setTitle("社保国保データ");
        let disp = new ShahokokuhoDisp(this.getContentElement());
        disp.set(shahokokuho);
        let cmap = this.setCommands(commandsTmpl);
        cmap.close.addEventListener("click", event => this.close());
    }

    set(shahokokuho) {
        this.disp.set(data);
        return this;
    }
}

