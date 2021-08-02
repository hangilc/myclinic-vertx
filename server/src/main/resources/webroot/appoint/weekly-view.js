import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import * as kanjidate from "../js/kanjidate.js";
import {hide} from "../js/dom-helper.js";

const timeSpansAM = [
    ["10:00", "10:20", {}],
    ["10:20", "10:40", {}],
    ["10:40", "11:00", {}],
    ["11:00", "11:20", {}],
    ["11:20", "11:40", {}],
    ["11:40", "12:00", {spare: true}],
];

const timeSpansPM = [
    ["14:00", "14:20", {}],
    ["14:20", "14:40", {}],
    ["14:40", "13:00", {}],
    ["13:00", "13:20", {}],
    ["13:20", "13:40", {}],
    ["13:40", "14:00", {spare: true}],
];

const tmpl = `
    <div>
        <div class="x-manage">
            <a href="javascript:void(0);" class="x-this-week-link">今週</a>
            <a href="javascript:void(0);" class="x-prev-week-link">前週</a>
            <span class="x-start-date-disp"></span> -
            <span class="x-end-date-disp"></span>
            <a href="javascript:void(0);" class="x-next-week-link">次週</a>
     </div>
        <div class="appoint-weekly-view d-flex x-weekly-view"></div>
    </div>
`;

export class WeeklyView {
    constructor(props) {
        this.props = props;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.updateUI();
        this.setupNextWeekLink(this.map.nextWeekLink);
        this.setupPrevWeekLink(this.map.prevWeekLink);
        this.setupThisWeekLink(this.map.thisWeekLink);
    }

    getStartingMonday(){
        return this.props.startingMonday;
    }

    setStartingMonday(sqldate){
        this.props.startingMonday = sqldate;
    }

    updateUI(){
        console.log(this.getStartingMonday());
        // this.updateStartDateUI();
        // this.updateEndDateUI();
        // this.map.weeklyView.innerHTML = "";
        // const self = this;
        // this.props.days.forEach(date => self.enterDate(date));
    }

    updateStartDateUI(){
        let date = this.props.days[0];
        let disp = kanjidate.sqldateToKanji(date);
        this.map.startDateDisp.innerText = disp;
    }

    updateEndDateUI(){
        let date = this.props.days[this.props.days.length-1];
        let disp = kanjidate.sqldateToKanji(date);
        this.map.endDateDisp.innerText = disp;
    }

    enterDate(date){
        let youbiIndex = kanjidate.youbiIndexOfSqldate(date);
        if( youbiIndex === 3 ){
            // nop
        } else {
            const col = new DayColumn(date);
            this.map.weeklyView.append(col.ele);
        }
    }

    setupNextWeekLink(e){
        e.addEventListener("click", event => {
            let date = kanjidate.advanceDays(this.getStartingMonday(), 7);
            this.setStartingMonday(date);
            this.updateUI();
        });
    }

    setupPrevWeekLink(e){
        e.addEventListener("click", event => {
            let date = kanjidate.advanceDays(this.getStartingMonday(), -7);
            this.setStartingMonday(date);
            this.updateUI();
        });
    }

    setupThisWeekLink(e){
        e.addEventListener("click", event => {
            let date = kanjidate.startOfWeek(kanjidate.todayAsSqldate(), 1);
            this.setStartingMonday(date);
            this.updateUI();
        });
    }

}

function selectWorkingDays(weekDays){
    return [1, 2, 4, 5, 6].map(i => weekDays[i]);
}

const dayColumnTmpl = `
    <div>
        <div class="appoint-date mb-2">
            <div class="x-date"></div>
            <div>（<span class="x-youbi"></span>）</div>
        </div>
        <div class="x-am-entries mb-2"></div>
        <div class="x-pm-entries mt-4"></div>
        <div class="appoint-date">
            <div class="x-date-bottom"></div>
            <div>（<span class="x-youbi-bottom"></span>）</div>
        </div>
    </div>
`;

class DayColumn {
    constructor(sqldate) {
        this.ele = createElementFrom(dayColumnTmpl);
        this.map = parseElement(this.ele);
        const dayData = kanjidate.sqldateToData(sqldate);
        [this.map.date, this.map.dateBottom].forEach(e =>
            e.innerText = `${dayData.month}月${dayData.day}日`);
        [this.map.youbi, this.map.youbiBottom].forEach(e =>
            e.innerText = dayData.youbi );
        const youbiIndex = kanjidate.youbiIndexOfSqldate(sqldate);
        this.ele.style.marginRight = (youbiIndex === 2) ? "3em" : "1em";
        timeSpansAM.forEach(ts => {
            const [start, end, opt] = ts;
            const kind = opt.spare ? "spare" : "empty";
            const entry = new Entry(start, end, kind, "（空き）");
            this.map.amEntries.append(entry.ele);
        });
        if( youbiIndex !== 6 ) {
            timeSpansPM.forEach(ts => {
                const [start, end, opt] = ts;
                const kind = opt.spare ? "spare" : "empty";
                const entry = new Entry(start, end, kind, "（空き）");
                this.map.pmEntries.append(entry.ele);
            });
        } else {
            hide(this.map.pmEntries);
        }
    }
}

const entryTmpl = `
    <div class="appoint-entry">
        <div class="time-part"><span class="x-start"></span> - <span class="x-end"></span></div>
        <div class="x-label"></div>
    </div>
`;

class Entry {
    constructor(start, end, kind, label) {
        this.ele = createElementFrom(entryTmpl);
        this.map = parseElement(this.ele);
        this.map.start.innerText = start;
        this.map.end.innerText = end;
        this.kind = kind;
        this.label = label;
        this.updateUI();
    }

    updateUI(){
        switch(this.kind){
            case "empty": {
                this.ele.classList.add("appoint-empty");
                break;
            }
            case "spare": {
                this.ele.classList.add("appoint-spare");
                break;
            }
            case "occupied": {
                this.ele.classList.add("appoint-occupied");
                break;
            }
            case "kenshin": {
                this.ele.classList.add("appoint-kenshin");
                break;
            }
        }
        this.map.label.innerText = truncatePatientName(this.label);
    }
}

function truncatePatientName(name){
    if( name.length > 7 ){
        return name.substring(0, 7) + "...";
    } else {
        return name;
    }
}
