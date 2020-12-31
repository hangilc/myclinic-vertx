import {Dialog} from "../../../js/dialog2.js";
import {parseElement} from "../../../js/parse-node.js";
import {PatientDisp} from "./patient-disp.js";
import {click, on} from "../../../js/dom-helper.js";
import * as kanjidate from "../../../js/kanjidate.js";

let bodyTmpl = `
    <div class="row">
        <div class="col-6">
            <select class="form-control x-select" size="7"></select>
            <div>
                <button class="btn btn-link x-prev">前へ</button>
                <button class="btn btn-link x-next">次へ</button>
            </div>
        </div>
        <div class="col-6 x-disp"></div>
    </div>
`;

let footerTmpl = `
    <button type="button" class="btn btn-primary x-enter">選択</button>
    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class RecentDialog extends Dialog {
    constructor(prop) {
        super({width: "700px"});
        this.prop = prop;
        this.rest = prop.rest;
        this.patient = null;
        this.page = 0;
        this.setTitle("最近の受診患者");
        this.getBody().innerHTML = bodyTmpl;
        let bmap = this.bmap = parseElement(this.getBody());
        this.disp = new PatientDisp();
        bmap.disp.append(this.disp.ele);
        on(bmap.select, "change", event => this.doSelect());
        click(bmap.prev, async event => await this.gotoPage(this.page - 1));
        click(bmap.next, async event => await this.gotoPage(this.page + 1));
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.enter, async event => await this.doEnter());
        click(fmap.cancel, event => this.close());
    }

    async init() {
        await this.gotoPage(0);
    }

    async gotoPage(page) {
        if (page < 0) {
            return;
        }
        let vps = await this.rest.listRecentVisitWithPatient(page);
        if (vps.length > 0) {
            this.setSelect(vps);
            this.page = page;
        }
    }

    setSelect(vps) {
        let select = this.bmap.select;
        select.innerHTML = "";
        vps.forEach(vp => {
            let opt = document.createElement("option");
            opt.innerText = optRep(vp);
            opt.data = vp.patient;
            select.append(opt);
        });
        select.scrollTop = 0;
    }

    doSelect() {
        let opt = this.bmap.select.querySelector("option:checked");
        if (opt) {
            let patient = opt.data;
            this.setPatient(patient);
        }
    }

    setPatient(patient) {
        this.patient = patient;
        this.disp.setPatient(patient);
    }

    async doEnter() {
        let patient = this.patient;
        if (!patient) {
            alert("患者が選択されていません。");
            return;
        }
        this.prop.endSession();
        this.prop.startSession(patient.patientId, 0);
        this.close();
    }
}

function optRep(vp) {
    let patient = vp.patient;
    let patientId = ("" + patient.patientId).padStart(4, "0");
    let at = kanjidate.sqldateToKanji(vp.visit.visitedAt.substring(0, 10));
    return `${patientId} ${patient.lastName} ${patient.firstName} (${at})`;
}