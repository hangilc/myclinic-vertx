import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {validFromRep, validUptoRep} from "../components/form-util.js";
import {ShahokokuhoBox} from "./shahokokuho-box.js";
import {KoukikoureiBox} from "./koukikourei-box.js";
import {KouhiBox} from "./kouhi-box.js";
import * as kanjidate from "../js/kanjidate.js";

let tmpl = `
<div>
    <span class="x-rep"></span>
    <button class="btn btn-link py-0 x-detail">詳細</button>
</div>
`;

export class HokenItem {
    constructor(rep, validFrom, validUpto){
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        let from = validFromRep(validFrom);
        let upto = validUptoRep(validUpto);
        this.map.rep.innerText = `${rep} ${from} - ${upto}`;
        this.map.detail.addEventListener("click", event => {
            this.ele.dispatchEvent(new Event("detail"));
        });
    }
}

export class HokenItemList {
    constructor(patientId, wrapper, workarea, isCurrent, rest){
        this.patientId = patientId;
        this.wrapper = wrapper;
        this.workarea = workarea;
        this.isCurrent = isCurrent;
        this.rest = rest;
    }

    broadcastChange(){
        this.workarea.dispatchEvent(new CustomEvent("hoken-changed", {bubbles: true}));
    }

    async populate(){
        this.wrapper.innerHTML = "";
        let hokenList = [];
        if( this.isCurrent ){
            hokenList = await this.rest.listAvailableAllHoken(this.patientId, kanjidate.todayAsSqldate());
        } else {
            hokenList = await this.rest.listAllHoken(this.patientId);
        }
        let repMap = await this.rest.batchResolveHokenRep(hokenList);
        let shahokokuhoIds = hokenList.shahokokuhoList.map(h => h.shahokokuhoId);
        let edabanMap = await this.rest.batchResolveEdaban(shahokokuhoIds)
        hokenList.shahokokuhoList.forEach(shahokokuho => {
            let rep = repMap[`shahokokuho:${shahokokuho.shahokokuhoId}`];
            let item = new HokenItem(rep, shahokokuho.validFrom, shahokokuho.validUpto);
            item.ele.addEventListener("detail", event => {
                let e = this.workarea.querySelector(`.shahokokuho-box-${shahokokuho.shahokokuhoId}`);
                if( e ){
                    this.workarea.prepend(e);
                } else {
                    let edaban = edabanMap[shahokokuho.shahokokuhoId] || "";
                    let box = new ShahokokuhoBox(shahokokuho, this.rest, edaban);
                    box.ele.addEventListener("updated", event => {
                        this.broadcastChange();
                    });
                    this.workarea.prepend(box.ele);
                }
            });
            this.wrapper.appendChild(item.ele);
        });
        hokenList.koukikoureiList.forEach(koukikourei => {
            let rep = repMap[`koukikourei:${koukikourei.koukikoureiId}`];
            let item = new HokenItem(rep, koukikourei.validFrom, koukikourei.validUpto);
            item.ele.addEventListener("detail", event => {
                let e = this.workarea.querySelector(`.koukikourei-box-${koukikourei.koukikoureiId}`);
                if( e ){
                    this.workarea.prepend(e);
                } else {
                    let box = new KoukikoureiBox(koukikourei, this.rest);
                    box.ele.addEventListener("updated", event => {
                        this.broadcastChange();
                    });
                    this.workarea.prepend(box.ele);
                }
            });
            this.wrapper.appendChild(item.ele);
        });
        hokenList.kouhiList.forEach(kouhi => {
            let rep = repMap[`kouhi:${kouhi.kouhiId}`];
            let item = new HokenItem(rep, kouhi.validFrom, kouhi.validUpto);
            item.ele.addEventListener("detail", event => {
                let e = this.workarea.querySelector(`.kouhi-box-${kouhi.kouhiId}`);
                if( e ){
                    this.workarea.prepend(e);
                } else {
                    let box = new KouhiBox(kouhi, this.rest);
                    box.ele.addEventListener("updated", event => {
                        this.broadcastChange();
                    });
                    this.workarea.prepend(box.ele);
                }
            });
            this.wrapper.appendChild(item.ele);
        });
    }
}

