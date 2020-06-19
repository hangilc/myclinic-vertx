import { Component } from "./component.js";

export class Title extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.textElement = map.text;
        this.menu = map.menu;
        ele.attr("data-component", 3);
    }

    async init(visit, classCurrentVisit, classTempVisit) {
        this.visit = visit;
        this.textElement.text(this.rep(visit.visitedAt));
        this.classCurrentVisit = classCurrentVisit;
        this.classTempVisit = classTempVisit;
        this.menu.delete.on("click",  event => this.doDelete());
        this.menu.tempVisit.on("click", event => this.doTempVisit());
        this.menu.untempVisit.on("click", event => this.doClearTempVisit());
        this.menu.meisai.on("click", event => this.doMeisai());
    }

    getVisitId(){
        return this.visit.visitId;
    }

    clearMark(){
        this.ele.removeClass(this.classTempVisit);
        this.ele.removeClass(this.classCurrentVisit);
    }

    markAsCurrent(){
        this.clearMark();
        this.ele.addClass(this.classCurrentVisit);
    }

    markAsTemp(){
        this.clearMark();
        this.ele.addClass(this.classTempVisit);
    }

    onDelete(cb){
        this.on("delete", (event, visitId) => cb(visitId));
    }

    async doDelete() {
        if (!confirm("この診療記録を削除しますか？")) {
            return;
        }
        this.trigger("delete", this.getVisitId());
    }

    onTempVisit(cb){
        this.on("temp-visit", (event, visitId) => cb(visitId));
    }

    doTempVisit(){
        this.trigger("temp-visit", this.getVisitId());
    }

    onClearTempVisit(cb){
        this.on("clear-temp-visit", event => cb());
    }

    doClearTempVisit(){
        this.trigger("clear-temp-visit");
    }

    async doMeisai(){
        let meisai = await this.rest.getMeisai(this.getVisitId());
        console.log(JSON.stringify(meisai, null, 2));
    }

    rep(sqldatetime) {
        let data = kanjidate.sqldatetimeToData(sqldatetime);
        let nen = (data.nen + "").padStart(2, "0");
        let month = (data.month + "").padStart(2, "0");
        let day = (data.day + "").padStart(2, "0");
        let hour = (data.hour + "").padStart(2, "0");
        let minute = (data.minute + "").padStart(2, "0");
        return `${data.gengou.name}${nen}年${month}月${day}日（${data.youbi}） ${hour}時${minute}分`;
    }

}

