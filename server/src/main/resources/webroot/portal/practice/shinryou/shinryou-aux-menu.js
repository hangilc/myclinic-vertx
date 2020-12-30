import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {click} from "../../../js/dom-helper.js";
import {ShinryouKensaDialog} from "./shinryou-kensa-dialog.js";
import {ShinryouSearchEnterWidget} from "../shinryou-search-enter-widget/shinryou-search-enter-widget.js";

let tmpl = `
    <div class="dropdown">
        <button type="button" class="btn btn-link dropdown-toggle"
                data-toggle="dropdown">その他
        </button>
        <div class="dropdown-menu x-shinryou-aux-menu">
            <a href="javascript:void(0)" class="x-kensa dropdown-item">検査</a>
            <a href="javascript:void(0)" class="x-search-enter dropdown-item">検索入力</a>
            <a href="javascript:void(0)" class="x-copy-all dropdown-item">全部コピー</a>
        </div>
    </div>
`;

export class ShinryouAuxMenu {
    constructor(prop, visitId, visitedAt, workarea){
        this.prop = prop;
        this.rest = prop.rest;
        this.visitId = visitId;
        this.visitedAt = visitedAt;
        this.workarea = workarea;
        this.ele = createElementFrom(tmpl);
        let map = parseElement(this.ele);
        click(map.kensa, async event => await this.doKensa());
        click(map.searchEnter, async event => await this.doSearchEnter());
        click(map.copyAll, async event => await this.doCopyAll());
    }

    async doKensa(){
        if( !this.prop.confirmManip(this.visitId, "検査を入力しますか") ){
            return;
        }
        let dialog = new ShinryouKensaDialog(this.visitId, this.rest);
        let result = await dialog.open();
        this.ele.dispatchEvent(new CustomEvent("batch-entered", {bubbles: true, detail: result}));
    }

    async doSearchEnter() {
        if( !this.prop.confirmManip(this.visitId, "診療行為を入力しますか") ){
            return;
        }
        let widget = new ShinryouSearchEnterWidget(this.rest, this.visitId, this.visitedAt);
        this.workarea.prepend(widget.ele);
        widget.initFocus();
    }

    async doCopyAll() {
        return Promise.resolve(undefined);
    }
}