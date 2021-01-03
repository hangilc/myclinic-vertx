import {Component} from "./component.js";
//import {shinryouSearchEnterWidgetFactory} from "./shinryou-search-enter-widget/shinryou-search-enter-widget.js";
import {createShohousenPdfForFax} from "./funs.js";
import {ShinryouKensaDialog} from "./shinryou/shinryou-kensa-dialog.js";
import {Charge} from "./charge/charge.js";
import {Title} from "./title/title.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {Text} from "./text/text.js";
import {TextEnter} from "./text/text-enter.js";
import {SendFax} from "./send-fax.js";
import {Hoken} from "./hoken/hoken.js";
import {ShinryouRegularDialog} from "./shinryou/shinryou-regular-dialog.js";
import {replaceNode} from "../../js/dom-helper.js";
import {ShinryouAuxMenu} from "./shinryou/shinryou-aux-menu.js";
import {Shinryou} from "./shinryou/shinryou.js";
import {DrugDisp} from "./drug-disp.js";
import {Conduct} from "./conduct/conduct.js";
import {ConductMenu} from "./conduct/conduct-menu.js";
import * as prop from "./app.js";
import {FaxProgress} from "./fax-progress.js";

let tmpl = `
    <div class="practice-record temp-visit-listener" data-visit-id="0">
        <div class="x-title"></div>
        <div class="row">
            <div class="col-sm-6 rp-1">
                <div class="x-text-wrapper"></div>
                <div class="x-command-wrapper record-left-commands mt-2">
                    <a href="javascript:void(0)" class="x-enter-text">［文章入力］</a>
                    <a href="javascript:void(0)" class="x-send-shohousen-fax">処方箋FAX</a>
                </div>
            </div>
            <div class="col-sm-6 lp-1">
                <div class="x-hoken-wrapper"></div>
                <div class="x-drug-mark d-none">Rp）</div>
                <div class="x-drug-wrapper"></div>
                <div class="form-inline">
                    <a href="javascript:void(0)" class="x-shinryou-menu">［診療行為］</a>
                    <div class="x-shinryou-aux-menu-placeholder"></div>
                </div>
                <div class="x-shinryou-widget-workarea"></div>
                <div class="x-shinryou-wrapper"></div>
                <div class="x-conduct-menu-placeholder"></div>
                <div class="x-conduct-workarea"></div>
                <div class="x-conduct-wrapper"></div>
                <div class="x-charge-wrapper"></div>
            </div>
        </div>
    </div>
`;

let pharmaTextRegex = /(.+)にファックス（(\+\d+)）で送付/;

export class Record {
    constructor(visitFull) {
        this.prop = prop;
        this.rest = prop.rest;
        let visit = visitFull.visit;
        this.visitId = visit.visitId;
        this.patientId = visit.patientId;
        this.visitedAt = visit.visitedAt;
        this.drugIndex = 1;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.ele.dataset.visitId = visitFull.visit.visitId;
        let visitDate = visit.visitedAt.substring(0, 10);
        let title = new Title(prop, visitFull.visit);
        this.map.title.append(title.ele);
        visitFull.texts.forEach(text => this.addText(text));
        this.setHoken(visitFull.hoken);
        replaceNode(this.map.shinryouAuxMenuPlaceholder,
            (new ShinryouAuxMenu(this.prop, this.visitId, this.visitedAt, this.map.shinryouWidgetWorkarea)).ele);
        visitFull.shinryouList.forEach(shinryouFull => this.addShinryou(shinryouFull, false));
        visitFull.drugs.forEach(drugFull => this.addDrug(drugFull));
        replaceNode(this.map.conductMenuPlaceholder,
            (new ConductMenu(this.prop, this.map.conductWorkarea, this.map.conductWrapper, this.visitId,
                visitDate)).ele);
        visitFull.conducts.forEach(conductFull => this.addConduct(conductFull));
        {
            const props = {
                charge: visitFull.charge,
                payment: null,
                isNoPay0410: false
            };
            const c = new Charge(props);
            this.map.chargeWrapper.append(c.ele);
        }
        this.map.enterText.addEventListener("click", event => this.doEnterText());
        this.map.sendShohousenFax.addEventListener("click", event => this.doSendShohousenFax());
        this.map.shinryouMenu.addEventListener("click", async event => await this.doRegularShinryou());
        if( this.prop.currentVisitId === this.visitId ){
            this.ele.classList.add("current-visit");
        }
        this.ele.addEventListener("temp-visit-changed", event => {
            if (this.prop.tempVisitId === this.getVisitId()) {
                this.ele.classList.add("temp-visit");
            } else {
                this.ele.classList.remove("temp-visit");
            }
        });
        this.ele.addEventListener("batch-entered", async event => {
            event.stopPropagation();
            await this.batchEnter(event.detail);
        });
        this.ele.addEventListener("text-entered", event => this.addText(event.detail));
        this.ele.addEventListener("shinryou-entered", event => {
            event.stopPropagation();
            event.detail.forEach(shinryouFull => this.addShinryou(shinryouFull, true));
        });
        this.ele.addEventListener("shinryou-deleted", event => {
            event.stopPropagation();
            let shinryouIds = event.detail;
            shinryouIds.forEach(shinryouId => {
                let e = findShinryouElement(this.map.shinryouWrapper, shinryouId);
                if( e ){
                    e.remove();
                }
            });
        });
        this.ele.addEventListener("conducts-entered", event => {
            event.detail.forEach(conductFull => this.addConduct(conductFull));
        });
    }

    getVisitId() {
        return this.visitId;
    }

    addText(text) {
        let textComponent = new Text(this.prop, text);
        this.map.textWrapper.append(textComponent.ele);
    }

    doEnterText() {
        let edit = new TextEnter(this.prop, this.getVisitId());
        edit.ele.addEventListener("entered", event => {
            edit.ele.remove();
            this.addText(event.detail);
        });
        edit.ele.addEventListener("cancel", event => edit.ele.remove());
        this.map.textWrapper.append(edit.ele);
    }

    async doSendShohousenFax() {
        let shohousenText;
        let pharmaName;
        let faxNumber;
        let texts = await this.prop.rest.listText(this.getVisitId());
        for (let text of texts) {
            let content = text.content;
            if (content.startsWith("院外処方")) {
                if (shohousenText) {
                    alert("処方箋が複数あります。");
                    return;
                }
                shohousenText = text;
            } else {
                let matches = pharmaTextRegex.exec(content);
                if (matches) {
                    if (pharmaName || faxNumber) {
                        alert("送付先薬局が複数あります。");
                        return;
                    }
                    pharmaName = matches[1];
                    faxNumber = matches[2];
                }
            }
        }
        if (!shohousenText) {
            alert("処方箋の入力を見つけられません。");
            return;
        }
        if (!(pharmaName && faxNumber)) {
            alert("送付先薬局が設定されていません。");
            return;
        }
        let textId = shohousenText.textId;
        let date = this.visitedAt.substring(0, 10);
        let pdfFilePath = await this.prop.rest.probeShohousenFaxImage(textId, date);
        if (!pdfFilePath) {
            if (!confirm("送信用の処方箋イメージを作成しますか？")) {
                return;
            }
            pdfFilePath = await createShohousenPdfForFax(shohousenText, this.prop.rest);
        }
        let sendFax = new SendFax(this.prop.rest, pdfFilePath, faxNumber);
        sendFax.ele.addEventListener("sent", async event => {
            let faxSid = event.detail;
            await this.startFaxProgress({
                textId: textId,
                faxNumber: faxNumber,
                pdfFile: pdfFilePath,
                faxSid: faxSid
            });
            sendFax.ele.remove();
        });
        this.map.commandWrapper.parentElement.insertBefore(sendFax.ele, this.map.commandWrapper);
    }

    async startFaxProgress(data){
        let {textId, faxNumber, pdfFile, faxSid} = data;
        let text = await prop.rest.getText(textId);
        let visit = await prop.rest.getVisit(text.visitId);
        let patient = await prop.rest.getPatient(visit.patientId);
        let title = `${patient.lastName}${patient.firstName} FAX`;
        let progress = new FaxProgress(prop.rest, title, faxNumber, pdfFile, faxSid);
        this.prop.map.generalWorkarea.append(progress.ele);
        progress.start();
    }

    setHoken(hoken){
        let c = new Hoken(this.prop.rest, this.patientId, this.visitedAt.substring(0, 10), hoken);
        this.map.hokenWrapper.innerHTML = "";
        this.map.hokenWrapper.append(c.ele);
        c.ele.addEventListener("enter", async event => {
            event.stopPropagation();
            let visit = await this.prop.rest.getVisit(this.getVisitId());
            Object.assign(visit, event.detail);
            await this.prop.rest.updateHoken(visit);
            let updatedHoken = await this.prop.rest.getHoken(this.visitId);
            this.setHoken(updatedHoken);
        });
    }

    async batchEnter(result){
        if (result.shinryouIds && result.shinryouIds.length > 0) {
            let shinryouFullList = await this.rest.listShinryouFullByIds(result.shinryouIds);
            shinryouFullList.forEach(sf => this.addShinryou(sf, true));
        }
        if (result.drugIds && result.drugIds.length > 0) {
            let drugFullList = await this.rest.listDrugFullByIds(result.drugIds);
            drugFullList.forEach(drugFull => this.addDrug(drugFull));
        }
        if (result.conductIds && result.conductIds.length > 0) {
            let conductFullList = await this.rest.listConductFullByIds(result.conductIds);
            console.log(conductFullList);
            conductFullList.forEach(conductFull => this.addConduct(conductFull));
        }
    }

    async doRegularShinryou(){
        let dialog = new ShinryouRegularDialog();
        let names = await dialog.open();
        if( names ){
            let result = await this.rest.batchEnterShinryouByNames(names, this.getVisitId());
            await this.batchEnter(result);
        }
    }

    addShinryou(shinryouFull, reorder=true){
        let shinryou = new Shinryou(this.prop, shinryouFull, this.getVisitId());
        if( reorder ){
            let shinryoucode = +shinryouFull.master.shinryoucode;
            let list = this.map.shinryouWrapper.querySelectorAll(".practice-shinryou");
            for(let node of list){
                let code = +node.dataset.shinryoucode;
                if( shinryoucode < code ){
                    node.parentNode.insertBefore(shinryou.ele, node);
                    return;
                }
            }
        }
        this.map.shinryouWrapper.append(shinryou.ele);
    }

    addDrug(drugFull){
        let index = this.drugIndex++;
        let d = new DrugDisp(drugFull, index);
        this.map.drugWrapper.append(d.ele);
    }

    addConduct(conductFull){
        let c = new Conduct(this.prop, conductFull);
        this.map.conductWrapper.append(c.ele);
    }

}

function findShinryouElement(wrapper, shinryouId){
    return wrapper.querySelector(`.practice-shinryou[data-shinryou-id='${shinryouId}']`);
}
