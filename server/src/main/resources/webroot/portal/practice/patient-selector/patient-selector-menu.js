import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {click} from "../../../js/dom-helper.js";
import {SearchDialog} from "./search-dialog.js";
import {WqueueDialog} from "./wqueue-dialog.js";

let tmpl = `
    <div class="dropdown">
        <button class="btn btn-secondary dropdown-toggle" type="button"
                data-toggle="dropdown" aria-haspopup="true"
                aria-expanded="false">
            患者選択
        </button>
        <div class="dropdown-menu x-menu" aria-labelledby="dropdownMenuButton">
            <a href="javascript:void(0)" class="x-wqueue dropdown-item mx-2">受付患者選択</a>
            <a href="javascript:void(0)" class="x-search dropdown-item mx-2">患者検索</a>
            <a href="javascript:void(0)" class="x-recent dropdown-item mx-2">最近の診察</a>
            <a href="javascript:void(0)" class="x-today dropdown-item mx-2">本日の診察</a>
            <a href="javascript:void(0)" class="x-prev dropdown-item mx-2">以前の診察</a>
        </div>
    </div>
`;

export class PatientSelectorMenu {
    constructor(prop) {
        this.prop = prop;
        this.rest = prop.rest;
        this.ele = createElementFrom(tmpl);
        let map = parseElement(this.ele);
        click(map.wqueue, async event => await this.doWqueue());
        click(map.search, async event => await this.doSearch());
        click(map.recent, async event => await this.doRecent());
        click(map.today, async event => await this.doToday());
        click(map.prev, async event => await this.doPrev());
    }

    async doWqueue() {
        let wqueue = await this.rest.listWqueueFullForExam();
        let dialog = new WqueueDialog(this.prop, wqueue);
        await dialog.open();
    }

    async doSearch() {
        let dialog = new SearchDialog(this.prop);
        await dialog.open(() => dialog.initFocus());
    }

    async doRecent() {
        return Promise.resolve(undefined);
    }

    async doToday() {
        return Promise.resolve(undefined);
    }

    async doPrev() {
        return Promise.resolve(undefined);
    }
}