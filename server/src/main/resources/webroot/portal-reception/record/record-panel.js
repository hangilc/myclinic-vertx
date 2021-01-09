import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {click, show, hide, on, setOnlyChild} from "../../js/dom-helper.js";
import {WqueueWidget} from "./wqueue-widget.js";
import {Record} from "./record.js";
import {PatientDisplay} from "./patient-display.js";
import {Nav} from "../../components/nav.js";
import {SearchWidget} from "./search-widget.js";
import {RecentWidget} from "./recent-widget.js";
import {ByDateWidget} from "./by-date-widget.js";

let tmpl = `
<div>
    <div class="mb-3 form-inline">
        <div class="h3">診療記録</div>
    </div>
    <div class="x-patient-wrapper"></div>
    <div class="row">
        <div class="col-9 x-main-pane">
            <div class="d-none nav"></div>
            <div class="x-records"></div>
            <div class="d-none nav"></div>
        </div>
        <div class="col-3 x-side-pane">
            <div class="dropdown mb-3">
                <button class="btn btn-secondary dropdown-toggle" type="button"
                        data-toggle="dropdown" aria-haspopup="true"
                        aria-expanded="false">
                    患者選択
                </button>
                <div class="dropdown-menu x-menu" aria-labelledby="dropdownMenuButton">
                    <a href="javascript:void(0)" class="x-wqueue dropdown-item mx-2">受付患者</a>
                    <a href="javascript:void(0)" class="x-search dropdown-item mx-2">患者検索</a>
                    <a href="javascript:void(0)" class="x-recent dropdown-item mx-2">最近の診察</a>
                    <a href="javascript:void(0)" class="x-by-date dropdown-item mx-2">日付別</a>
                </div>
            </div>
            <div class="x-side-pane-workarea"></div>
        </div>
    </div>
</div>
`;

export class RecordPanel {
    constructor(rest) {
        this.props = {
            patient: null,
            page: 0,
            totalPages: 0,
            visitsFulls: []
        };
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        const map = this.map = parseElement(this.ele);
        this.navs = Array.from(this.getNavElements()).map(e => {
            const nav = new Nav(e);
            nav.setTriggerFun(page => this.gotoPage(page));
            return nav;
        });
        click(map.wqueue, async event => await this.doWqueue());
        click(map.search, event => this.doSearch());
        click(map.recent, async event => await this.doRecent());
        click(map.byDate, async event => await this.doByDate());
        on(this.ele, "patient-clicked", async event => {
            this.props.patient = event.detail;
            await this.gotoPage(0);
        });
    }

    async doWqueue(){
        const patients = (await this.rest.listWqueueFull()).map(df => df.patient);
        const w = new WqueueWidget(patients);
        this.setWorkarea(w.ele);
    }

    doSearch(){
        const w = new SearchWidget(this.rest);
        this.setWorkarea(w.ele);
        w.initFocus();
    }

    async doRecent(){
        const w = new RecentWidget(this.rest);
        await w.init();
        this.setWorkarea(w.ele);
    }

    async doByDate(){
        const w = new ByDateWidget(this.rest);
        await w.init();
        this.setWorkarea(w.ele);
    }

    setWorkarea(e){
        let workarea = this.map.sidePaneWorkarea;
        workarea.innerHTML = "";
        workarea.append(e);
    }

    async gotoPage(page){
        this.props.page = page;
        await this.fetchVisits();
        this.updatePatientUI();
        this.updateNavUI();
        await this.updateRecordUI();
    }

    async fetchVisits(){
        const recordPage = await this.rest.listVisit(this.props.patient.patientId, this.props.page);
        this.props.visitFulls = recordPage.visits;
        this.props.page = recordPage.page;
        this.props.totalPages = recordPage.totalPages;
    }

    updatePatientUI(){
        const disp = new PatientDisplay(this.props.patient);
        setOnlyChild(this.map.patientWrapper, disp.ele);
    }

    getNavElements(){
        return this.ele.querySelectorAll(".nav");
    }

    updateNavUI(){
        if( this.props.totalPages > 1 ){
            this.navs.forEach(nav => nav.adaptToPage(this.props.page, this.props.totalPages));
            this.getNavElements().forEach(e => show(e));
        } else {
            this.getNavElements().forEach(e => hide(e));
        }
    }

    async updateRecordUI(){
        const wrapper = this.map.records;
        wrapper.innerHTML = "";
        this.props.visitFulls.forEach(visitFull => {
            const record = new Record(visitFull);
            wrapper.append(record.ele);
        });
        window.scrollTo(0, 0);
    }

}

