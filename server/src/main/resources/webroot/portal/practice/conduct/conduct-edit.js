import {Widget} from "../../../js/widget.js";
import {ConductDisp} from "../../../components/conduct-disp.js";
import {parseElement} from "../../../js/parse-node.js";
import {click} from "../../../js/dom-helper.js";

let footerTmpl = `
    <button class="btn btn-secondary x-delete">削除</button>
    <button class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class ConductEdit extends Widget {
    constructor(rest, conductFull) {
        super();
        this.rest = rest;
        this.conductId = conductFull.conduct.conductId;
        this.setTitle("処置の編集");
        let disp = new ConductDisp(conductFull, true);
        this.getBody().append(disp.ele);
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.delete, async event => await this.doDelete());
        click(fmap.cancel, event => this.close());
    }

    async doDelete(){
        if( !confirm("この処置を削除していいですか？") ){
            return;
        }
        await this.rest.deleteConduct(this.conductId);
        this.close("deleted");
    }
}