import { Component } from "../component.js";
import * as kanjidate from "../../js/kanjidate.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {on, click} from "../../../js/dom-helper.js";
import {VisitMeisaiDialog} from "../visit-meisai-dialog.js";
import {titleRep} from "./title-funs.js";

let tmpl = `
<div class="practice-title mt-2 form-inline">
    <div class="x-text"></div>
    <div class="dropdown ml-auto">
        <button class="btn btn-link dropdown-toggle" type="button"
                data-toggle="dropdown" aria-haspopup="true"
                aria-expanded="false">
            操作
        </button>
        <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
            <a href="javascript:void(0)" class="x-delete dropdown-item">この診察を削除</a>
            <a href="javascript:void(0)" class="x-temp-visit dropdown-item">暫定診察に設定</a>
            <a href="javascript:void(0)" class="x-untemp-visit dropdown-item">暫定診察の解除</a>
            <a href="javascript:void(0)" class="x-meisai dropdown-item">診療明細</a>
            <a href="javascript:void(0)" class="x-futan-wari-override dropdown-item">負担割オーバーライド</a>
            <a href="javascript:void(0)" class="x-add-to-no-pay-list dropdown-item">未収リストへ</a>
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
        this.map.text.innerText = titleRep(visit.visitedAt);
        this.bindDelete();
        this.bindTempVisit();
        this.bindUntempVisit();
        this.bindMeisai();
        this.bindFutanWariOverride();
        this.bindAddToNoPayList();
    }

    bindDelete(){
        click(this.map.delete, event => this.doDelete());
    }

    bindTempVisit(){
        click(this.map.tempVisit, event => this.doTempVisit());
    }

    bindUntempVisit(){
        click(this.map.untempVisit, event => this.doClearTempVisit());
    }

    bindMeisai(){
        click(this.map.meisai, async event => await this.doMeisai());
    }

    bindFutanWariOverride(){
        click(this.map.futanWariOverride, async event => await this.doFutanWariOverride());
    }

    bindAddToNoPayList(){
        click(this.map.addToNoPayList, event => this.doAddToNoPayList());
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

    doTempVisit(){
        this.ele.dispatchEvent(new CustomEvent("set-temp-visit", {bubbles: true, detail: this.getVisitId()}));
    }

    doClearTempVisit(){
        this.ele.dispatchEvent(new CustomEvent("clear-temp-visit", {bubbles: true}));
    }

    doAddToNoPayList(){
        this.ele.dispatchEvent(new CustomEvent("add-to-no-pay-list",
            {bubbles: true, detail: this.getVisitId()}));
    }

    async doMeisai(){
        let meisai = await this.prop.rest.getMeisai(this.getVisitId());
        let dialog = new VisitMeisaiDialog(meisai);
        await dialog.open();
    }

    // rep(sqldatetime) {
        // let data = kanjidate.sqldatetimeToData(sqldatetime);
        // let nen = (data.nen + "").padStart(2, "0");
        // let month = (data.month + "").padStart(2, "0");
        // let day = (data.day + "").padStart(2, "0");
        // let hour = (data.hour + "").padStart(2, "0");
        // let minute = (data.minute + "").padStart(2, "0");
        // return `${data.gengou.name}${nen}年${month}月${day}日（${data.youbi}） ${hour}時${minute}分`;
    // }

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
        await this.prop.rest.updateVisitAttr(this.visit.visitId, JSON.stringify(attr));
    }

}

