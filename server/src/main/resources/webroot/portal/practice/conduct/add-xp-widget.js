import {Widget} from "../../../js/widget.js";
import {parseElement} from "../../../js/parse-node.js";
import {click} from "../../../js/dom-helper.js";

let bodyTmpl = `
    <div class="form-inline">
        <select class="form-control mr-2 x-label-select">
            <option>胸部単純Ｘ線</option>
            <option>腹部単純Ｘ線</option>
        </select>
        <select class="form-control x-film-select">
            <option>大角</option>
            <option>四ツ切</option>
        </select>
    </div>
`;

let footerTmpl = `
    <button class="btn btn-primary x-enter">入力</button>
    <button class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class AddXpWidget extends Widget {
    constructor(rest, visitId) {
        super();
        this.rest = rest;
        this.visitId = visitId;
        this.setTitle("Ｘ線検査入力");
        this.getBody().innerHTML = bodyTmpl;
        this.bmap = parseElement(this.getBody());
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.enter, async event => await this.doEnter());
        click(fmap.cancel, event => this.close());
    }

    async doEnter(){
        let label = this.bmap.labelSelect.value;
        let film = this.bmap.filmSelect.value;
        let result = await this.rest.enterXp(this.visitId, label, film);
        this.ele.dispatchEvent(new CustomEvent("batch-entered", {bubbles: true, detail: result}));
        this.close(true);
    }
}
