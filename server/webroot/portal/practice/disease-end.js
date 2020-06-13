import {Component} from "../js/component.js";
import * as DiseaseUtil from "../js/disease-util.js";
import * as consts from "../js/consts.js";
import * as kanjidate from "../js/kanjidate.js";

export class DiseaseEnd extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.listElement = map.list;
        this.dateInputElement = map.dateInput;
        this.endReasonFormElement = map.endReasonForm;
        this.enterElement = map.enter;
        this.advanceWeekElement = map.dateCommands.advanceWeek;
        this.todayElement = map.dateCommands.today;
        this.endOfMonthElement = map.dateCommands.endOfMonth;
        this.endOfLastMonthElement = map.dateCommands.endOfLastMonth;
    }

    init() {
        this.listElement.on("change", "input[type=checkbox]", event => this.doCheckChanged());
        this.enterElement.on("click", event => this.doEnter());
        this.advanceWeekElement.on("click", event =>
            this.modifyDate(d => kanjidate.advanceDays(d, 7)));
        this.todayElement.on("click", event => this.dateInputElement.val(kanjidate.todayAsSqldate()));
        this.endOfMonthElement.on("click", event =>
            this.modifyDate(d => kanjidate.toEndOfMonth(d)));
        this.endOfLastMonthElement.on("click", event =>
            this.dateInputElement.val(kanjidate.endOfLastMonth()));
    }

    modifyDate(f) {
        let date = this.dateInputElement.val();
        if (date) {
            let d = f(date);
            this.dateInputElement.val(d);
        }
    }

    set(diseaseFulls) {
        for (let df of diseaseFulls) {
            let e = this.createCheckUnit(df);
            this.listElement.append(e);
        }
    }

    containsSusp(diseaseFull) {
        let suspcode = consts.suspMaster.shuushokugocode;
        if (diseaseFull.adjList) {
            for (let adjFull of diseaseFull.adjList) {
                if (adjFull.diseaseAdj.shuushokugocode === suspcode) {
                    return true;
                }
            }
        }
        return false;
    }

    convertToReq(diseaseFull, endReason, endDate) {
        if (endReason === consts.DiseaseEndReasonCured && this.containsSusp(diseaseFull)) {
            endReason = consts.DiseaseEndReasonStopped;
        }
        return {
            diseaseId: diseaseFull.disease.diseaseId,
            endDate,
            endReason
        }
    }

    getCheckedEndReason() {
        return this.endReasonFormElement.find("input[type=radio]:checked").val();
    }

    async doEnter() {
        let endDate = this.dateInputElement.val();
        if (!endDate) {
            alert("終了日が指定されていません。");
            return;
        }
        let endReason = this.getCheckedEndReason();
        let reqs = this.checkedDiseases().map(df => this.convertToReq(df, endReason, endDate));
        await this.rest.batchUpdateDiseaseEndReason(reqs);
    }

    doCheckChanged() {
        let date = "";
        for (let df of this.checkedDiseases()) {
            let startDate = df.disease.startDate;
            if (startDate > date) {
                date = startDate;
            }
        }
        this.dateInputElement.val(date);
    }

    createCheckUnit(diseaseFull) {
        let e = $("<div>");
        e.append(this.createCheck(diseaseFull));
        let label = $("<span>", {
            class: "ml-1"
        });
        label.text(DiseaseUtil.diseaseRep(diseaseFull) + DiseaseUtil.datePart(diseaseFull.disease));
        e.append(label);
        return e;
    }

    createCheck(diseaseFull) {
        let check = $("<input>", {
            type: "checkbox"
        });
        check.data("data", diseaseFull);
        return check;
    }

    checkedDiseases() {
        let checked = this.listElement.find("input[type=checkbox]:checked");
        let result = [];
        for (let i = 0; i < checked.length; i++) {
            let e = checked.slice(i, i + 1);
            let df = e.data("data");
            result.push(df);
        }
        return result;
    }
}