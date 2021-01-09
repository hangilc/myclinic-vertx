import {Widget} from "../../js/widget.js";
import {Item} from "./item.js";
import * as kanjidate from "../../js/kanjidate.js";
import {NoEndNav} from "./no-end-nav.js";

const bodyTmpl = `
    <div class="x-nav"></div>
    <div class="x-list"></div>
`;

export class RecentWidget extends Widget {
    constructor(rest) {
        super();
        this.setTitle("最近の診察");
        this.rest = rest;
        this.props = {
            page: 0,
            visitPatients: []
        };
        this.itemsPerPage = 10;
        this.bmap = this.setBody(bodyTmpl);
        this.nav = new NoEndNav(this.bmap.nav);
        this.nav.setTriggerFun(async page => {
            this.props.page = page;
            await this.fetchPage();
            this.updateUI();
        });
    }

    async init() {
        await this.fetchPage();
        this.updateUI();
    }

    async fetchPage() {
        this.props.visitPatients = await this.rest.listRecentVisitWithPatient(this.props.page, this.itemsPerPage);
    }

    updateUI(){
        this.nav.adaptToPage(this.props.page);
        this.bmap.list.innerHTML = "";
        this.props.visitPatients.forEach(visitPatient => {
            const item = new Item(visitPatient.patient);
            item.setDetail(kanjidate.sqldateToKanji(visitPatient.visit.visitedAt.substring(0, 10)));
            this.bmap.list.append(item.ele);
        });
    }
}