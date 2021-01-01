import {Widget} from "../widget2.js";
import {parseElement} from "../../../js/parse-node.js";
import {click} from "../../../js/dom-helper.js";
import * as kanjidate from "../../../js/kanjidate.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {DateInputDialog} from "../../../date-input/date-input-dialog.js";

let calIcon = `
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" 
        class="bi bi-calendar" viewBox="0 0 16 16">
        <path d="M3.5 0a.5.5 0 0 1 .5.5V1h8V.5a.5.5 0 0 1 1 0V1h1a2 2 0 0 1 2 2v11a2 
            2 0 0 1-2 2H2a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h1V.5a.5.5 0 0 1 .5-.5zM1 
            4v10a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V4H1z"/>
    </svg>`;

let leftArrow = `
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi 
        bi-arrow-left-square" viewBox="0 0 16 16">
        <path fill-rule="evenodd" d="M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8zm15 0A8 8 0 1 1 0 
            8a8 8 0 0 1 16 0zm-4.5-.5a.5.5 0 0 1 0 1H5.707l2.147 
            2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z"/>
    </svg>
`;

let rightArrow = `
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi 
        bi-arrow-right-circle" viewBox="0 0 16 16">
        <path fill-rule="evenodd" d="M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8zm15 0A8 8 0 1 1 0 
            8a8 8 0 0 1 16 0zM4.5 7.5a.5.5 0 0 0 0 1h5.793l-2.147 2.146a.5.5 
            0 0 0 .708.708l3-3a.5.5 0 0 0 0-.708l-3-3a.5.5 0 1 0-.708.708L10.293 7.5H4.5z"/>
    </svg>
`;

let bodyTmpl = `
    <div class="mb-2"><span class="x-date"></span></div>
    <div class="mb-2">
        <a href="javascript:void(0)" class="text-secondary mr-1 x-date-dialog">${calIcon}</a>
        <a href="javascript:void(0)" class="text-secondary mr-1 x-prev">${leftArrow}</a>
        <a href="javascript:void(0)" class="text-secondary mr-1 x-next">${rightArrow}</a>
        <a href="javascript:void(0)" class="text-secondary mr-1 x-today" style="font-size: 14px">本日</a>
    </div>
    <div class="x-patients"></div>
`;

let footerTmpl = `
    <button class="btn btn-link x-close">閉じる</button>
`;

export class ByDateWidget extends Widget {
    constructor(prop) {
        super();
        this.prop = prop;
        this.rest = prop.rest;
        this.sqldate = null;
        this.today = kanjidate.todayAsSqldate();
        this.setTitle("日付別患者リスト");
        this.getBody().innerHTML = bodyTmpl;
        let bmap = this.bmap = parseElement(this.getBody());
        click(bmap.dateDialog, async event => await this.doDateDialog());
        click(bmap.prev, async event => await this.doPrev());
        click(bmap.next, async event => await this.doNext());
        click(bmap.today, async event => await this.doToday());
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.close, event => this.close());
        this.bindPatientsArea(this.bmap.patients);
    }

    async doDateDialog(){
        let dialog = new DateInputDialog();
        dialog.setDate(this.sqldate);
        let result = await dialog.open();
        if( result ){
            await this.setDate(result);
        }
    }

    async doPrev(){
        let advanced = kanjidate.advanceDays(this.sqldate, -1);
        if( advanced !== this.sqldate ){
            await this.setDate(advanced);
        }
    }

    async doNext(){
        let advanced = kanjidate.advanceDays(this.sqldate, 1);
        if( advanced !== this.sqldate ){
            await this.setDate(advanced);
        }
    }

    async doToday(){
        this.setDate(this.today);
    }

    bindPatientsArea(e){
        e.addEventListener("select-patient", event => {
            event.stopPropagation();
            let patientId = event.detail;
            this.prop.endSession();
            this.prop.startSession(patientId);
            e.querySelectorAll(".patient-list-item").forEach(
                e => e.dispatchEvent(new Event("clear-bold")));
            e.querySelector(`.patient-list-item[data-patient-id='${patientId}']`)
                .dispatchEvent(new Event("bold"));
        });
    }

    async setDate(sqldate){
        console.log(sqldate);
        this.sqldate = sqldate;
        let vps = await this.rest.listVisitPatientAt(sqldate);
        let patients = vps.map(vp => vp.patient);
        this.setPatients(patients);
        this.bmap.date.innerText = this.dateRep(sqldate);
    }

    setPatients(patients){
        let wrapper = this.bmap.patients;
        wrapper.innerHTML = "";
        patients.forEach(patient => {
            let item = new Item(patient);
            wrapper.append(item.ele);
        })
    }

    dateRep(sqldate){
        let s = kanjidate.sqldateToKanji(sqldate, {youbi: true});
        if( sqldate === this.today ){
            s += " 本日";
        }
        return s;
    }

}

let itemTmpl = `
    <div class="patient-list-item" data-patient-id="0">
        <a href="javascript:void(0)" class="x-link"><span class="x-label"></span></a>
    </div>
`;

class Item {
    constructor(patient) {
        this.patient = patient;
        this.ele = createElementFrom(itemTmpl);
        this.ele.dataset.patientId = patient.patientId;
        let map = this.map = parseElement(this.ele);
        let patientIdRep = ("" + patient.patientId).padStart(4, "0");
        map.label.innerText = `(${patientIdRep}) ${patient.lastName} ${patient.firstName}`;
        this.ele.addEventListener("bold", event => this.bold());
        this.ele.addEventListener("clear-bold", event => this.clearBold());
        click(map.link, event => this.ele.dispatchEvent(new CustomEvent("select-patient",
            {bubbles: true, detail: patient.patientId})));
    }

    getPatient() {
        return this.patient;
    }

    clearBold() {
        this.map.label.classList.remove("font-weight-bold");
    }

    bold() {
        this.map.label.classList.add("font-weight-bold");
    }
}
