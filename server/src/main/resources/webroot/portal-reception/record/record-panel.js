import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {click, on} from "../../js/dom-helper.js";
import {WqueueWidget} from "./wqueue-widget.js";
import {Record} from "./record.js";

let tmpl = `
<div>
    <div class="mb-3 form-inline">
        <div class="h3">診療記録</div>
    </div>
    <div class="row">
        <div class="col-9 x-main-pane">
            <div class="x-records"></div>
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
            visitsFulls: []
        };
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        const map = this.map = parseElement(this.ele);
        click(map.wqueue, async event => await this.doWqueue());
        click(map.search, async event => await this.doSearch());
        click(map.recent, async event => await this.doRecent());
        click(map.byDate, async event => await this.doByDate());
        on(this.ele, "patient-clicked", async event => {
            this.props.patient = event.detail;
            this.props.page = 0;
            await this.fetchVisits();
            await this.updateRecordUI();
        });
    }

    async doWqueue(){
        const patients = (await this.rest.listWqueueFull()).map(df => df.patient);
        const w = new WqueueWidget(patients);
        this.setWorkarea(w.ele);
    }

    async doSearch(){

    }

    async doRecent(){

    }

    async doByDate(){

    }

    setWorkarea(e){
        let workarea = this.map.sidePaneWorkarea;
        workarea.innerHTML = "";
        workarea.append(e);
    }

    async fetchVisits(){
        const recordPage = await this.rest.listVisit(this.props.patient.patientId, this.props.page);
        this.props.visitFulls = recordPage.visits;
    }

    async updateRecordUI(){
        const wrapper = this.map.records;
        wrapper.innerHTML = "";
        this.props.visitFulls.forEach(visitFull => {
            const record = new Record(visitFull);
            wrapper.append(record.ele);
        });
    }

}

