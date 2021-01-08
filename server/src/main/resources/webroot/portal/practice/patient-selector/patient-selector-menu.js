import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {click} from "../../../js/dom-helper.js";
import {SearchDialog} from "./search-dialog.js";
import {WqueueDialog} from "./wqueue-dialog.js";
import {RecentDialog} from "./recent-dialog.js";
import {ByDateWidget} from "./by-date-widget.js";
import * as kanjidate from "../../../js/kanjidate.js";
import * as app from "../app.js";

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
            <a href="javascript:void(0)" class="x-by-date dropdown-item mx-2">日付別</a>
        </div>
    </div>
`;

export class PatientSelectorMenu {
    constructor() {
        this.prop = app;
        this.rest = app.rest;
        this.workarea = app.map.generalWorkarea;
        this.ele = createElementFrom(tmpl);
        let map = parseElement(this.ele);
        click(map.wqueue, async event => await this.doWqueue());
        click(map.search, async event => await this.doSearch());
        click(map.recent, async event => await this.doRecent());
        click(map.byDate, async event => await this.doByDate());
    }

    async doWqueue() {
        let wqueue = await this.rest.listWqueueFullForExam();
        let dialog = new WqueueDialog(wqueue);
        await dialog.open();
    }

    async doSearch() {
        let dialog = new SearchDialog(this.prop);
        await dialog.open(() => dialog.initFocus());
    }

    async doRecent() {
        let dialog = new RecentDialog();
        await dialog.init();
        await dialog.open();
    }

    async doByDate() {
        let widget = new ByDateWidget();
        await widget.setDate(kanjidate.todayAsSqldate());
        console.log(this.workarea, widget);
        this.workarea.append(widget.ele);
    }
}