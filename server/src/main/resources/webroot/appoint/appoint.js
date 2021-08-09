import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import * as kanjidate from "../js/kanjidate.js";
import {WeeklyView} from "./weekly-view.js";

const tmplSave = `
    <div>
        <div>
            <div class="appoint-date">
                <div>10月12日</div>
                <div>（水）</div>
            </div>
            <div class="appoint-entry appoint-empty">
                <div class="time-part">10:00 - 10:20</div>
                <div>（空き）</div>
            </div>
            <div class="appoint-entry appoint-occupied">
                <div>10:20 - 10:40</div>
                <div style="font-size:80%">田中一郎二郎三...</div>
            </div>
            <div class="appoint-entry appoint-empty">
                <div>10:00 - 10:20</div>
                <div>（空き）</div>
            </div>
            <div class="appoint-entry appoint-empty">
                <div>10:00 - 10:20</div>
                <div>（空き）</div>
            </div>
            <div class="appoint-entry appoint-empty">
                <div>10:00 - 10:20</div>
                <div>（空き）</div>
            </div>
            <div class="appoint-entry appoint-spare">
                <div>11:40 - 12:00</div>
                <div>（予備）</div>
            </div>
            
            <div class="appoint-space"></div>
            
            <div class="appoint-entry appoint-kenshin">
                <div>10:00 - 10:20</div>
                <div>（空き）</div>
            </div>
            <div class="appoint-entry appoint-kenshin">
                <div>10:00 - 10:20</div>
                <div>（空き）</div>
            </div>
            <div class="appoint-entry appoint-empty">
                <div>10:00 - 10:20</div>
                <div>（空き）</div>
            </div>
            <div class="appoint-entry appoint-empty">
                <div>10:00 - 10:20</div>
                <div>（空き）</div>
            </div>
            <div class="appoint-entry appoint-empty">
                <div>10:00 - 10:20</div>
                <div>（空き）</div>
            </div>
            <div class="appoint-entry appoint-spare">
                <div>15:40 - 16:00</div>
                <div>（予備）</div>
            </div>
        </div>
    </div>
`;

const tmpl = `
    <div></div>
`;

export class Appoint {
    constructor(props) {
        this.props = Object.create(props);
        Object.assign(this.props, {
            startingMonday: this.props.startingMonday || startingMonday(kanjidate.todayAsSqldate())
        });
        console.log(props.appointRest);
        this.ele = createElementFrom(tmpl)
        this.map = parseElement(this.ele);
    }

    async init(){
        await this.showWeeklyView(this.props);
    }

    async showWeeklyView(sqldate){
        const view = new WeeklyView(sqldate);
        await view.updateDerivedData();
        this.ele.innerHTML = "";
        this.ele.append(view.ele);
    }
}

function startingMonday(sqldate){
    return kanjidate.startOfWeek(sqldate, 1);
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
        this.map.innerText = truncatePatientName(this.label);
    }
}

function truncatePatientName(name){
    if( name.length > 7 ){
        return name.substring(0, 7) + "...";
    } else {
        return name;
    }
}

function listWeekDays(sqldate){
    const start = kanjidate.startOfWeek(sqldate);
    return kanjidate.consecutiveDays(start, 7);
}
