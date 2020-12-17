import { Component } from "../component.js";
import * as kanjidate from "../../js/kanjidate.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {on} from "../../../js/dom-helper.js";

let tmpl = `
<div className="mt-2 practice-title form-inline">
    <div className="x-text"></div>
    <div className="dropdown ml-auto">
        <button className="btn btn-link dropdown-toggle" type="button"
                data-toggle="dropdown" aria-haspopup="true"
                aria-expanded="false">
            操作
        </button>
        <div className="dropdown-menu x-menu_" aria-labelledby="dropdownMenuButton">
            <a href="javascript:void(0)" className="x-delete dropdown-item">この診察を削除</a>
            <a href="javascript:void(0)" className="x-temp-visit dropdown-item">暫定診察に設定</a>
            <a href="javascript:void(0)" className="x-untemp-visit dropdown-item">暫定診察の解除</a>
            <a href="javascript:void(0)" className="x-meisai dropdown-item">診療明細</a>
            <a href="javascript:void(0)" className="x-futan-wari-override dropdown-item">負担割オーバーライド</a>
        </div>
    </div>
</div>
`;

export class Title {
    constructor(prop, visit) {
        this.prop = prop;
        this.visit = visit;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
    }

    async init(visit, visitMeisaiDialogFactory) {
        this.visit = visit;
        this.textElement.text(this.rep(visit.visitedAt));
        this.visitMeisaiDialogFactory = visitMeisaiDialogFactory;
        this.menu.delete.on("click",  event => this.doDelete());
        this.menu.tempVisit.on("click", event => this.doTempVisit());
        this.menu.untempVisit.on("click", event => this.doClearTempVisit());
        this.menu.meisai.on("click", event => this.doMeisai());
        this.menu.futanWariOverride.on("click", async event => await this.doFutanWariOverride());
    }

    getVisitId(){
        return this.visit.visitId;
    }

    onDelete(cb){
        on(this.ele, "delete", event => cb(event.detail));
        //this.on("delete", (event, visitId) => cb(visitId));
    }

    async doDelete() {
        if (!confirm("この診療記録を削除しますか？")) {
            return;
        }
        this.ele.dispatchEvent(new CustomEvent("delete", {detail: this.getVisitId()}));
        //this.trigger("delete", this.getVisitId());
    }

    onTempVisit(cb){
        on(this.ele, "temp-visit", )
        //this.on("temp-visit", (event, visitId) => cb(visitId));
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
        let dialog = this.visitMeisaiDialogFactory.create(meisai);
        await dialog.open();
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

    async doFutanWariOverride(){
        let p = "負担割";
        let input = prompt("負担割");
        if( input == null ){
            return;
        }
        let futanWari = null;
        if( input !== "" ){
            futanWari = parseInt(input);
            if( isNaN(futanWari) ){
                alert("Invalid input: " + input);
                return;
            }
        }
        let attr = JSON.parse(this.visit.attributes || "{}");
        attr.futanWari = futanWari;
        await this.rest.updateVisitAttr(this.visit.visitId, JSON.stringify(attr));
    }

}

