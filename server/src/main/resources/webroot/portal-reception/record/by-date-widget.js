import {Widget} from "../../js/widget.js";
import * as kanjidate from "../../js/kanjidate.js";
import {clear, click} from "../../js/dom-helper.js";
import {Item} from "./item.js";
import {DateInputDialog} from "../../date-input/date-input-dialog.js";

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

export class ByDateWidget extends Widget {
    constructor(rest) {
        super();
        this.rest = rest;
        this.today = kanjidate.todayAsSqldate();
        this.props = {
            date: this.today,
            patients: []
        };
        this.setTitle("日付別患者選択");
        const bmap = this.bmap = this.setBody(bodyTmpl);
        click(bmap.dateDialog, async event => await this.doDateDialog());
        click(bmap.prev, async event => await this.doPrev());
        click(bmap.next, async event => await this.doNext());
        click(bmap.today, async event => await this.doToday());
    }

    async init(){
        await this.setDate(kanjidate.todayAsSqldate());
    }

    async setDate(sqldate){
        this.props.date = sqldate;
        this.props.patients = (await this.rest.listVisitPatientAt(this.props.date)).map(vps => vps.patient);
        this.updateUI();
    }

    updateUI(){
        this.updateDateUI();
        this.updatePatientsUI();
    }

    updateDateUI(){
        let str = `${kanjidate.sqldateToKanji(this.props.date, {"youbi": true})}`;
        if( this.props.date === this.today ){
            str += "（今日）";
        }
        this.bmap.date.innerText = str;
    }

    updatePatientsUI(){
        clear(this.bmap.patients);
        this.props.patients.forEach(patient => {
            const item = new Item(patient);
            this.bmap.patients.append(item.ele);
        });
    }

    async doDateDialog(){
        let dialog = new DateInputDialog();
        dialog.setDate(this.props.date);
        let result = await dialog.open();
        if( result ){
            await this.setDate(result);
        }
    }

    async doPrev(){
        let advanced = kanjidate.advanceDays(this.props.date, -1);
        if( advanced !== this.props.date ){
            await this.setDate(advanced);
        }
    }

    async doNext(){
        let advanced = kanjidate.advanceDays(this.props.date, 1);
        if( advanced !== this.props.date ){
            await this.setDate(advanced);
        }
    }

    async doToday(){
        await this.setDate(this.today);
    }

}