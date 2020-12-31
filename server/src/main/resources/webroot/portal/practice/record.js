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
<!--                <a href="javascript:void(0)" class="x-conduct-menu">［処置］</a>-->
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
    constructor(prop, visitFull) {
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
        let title = new Title(prop, visitFull.visit);
        this.map.title.append(title.ele);
        visitFull.texts.forEach(text => this.addText(text));
        this.setHoken(visitFull.hoken);
        replaceNode(this.map.shinryouAuxMenuPlaceholder,
            (new ShinryouAuxMenu(this.prop, this.visitId, this.visitedAt, this.map.shinryouWidgetWorkarea)).ele);
        visitFull.shinryouList.forEach(shinryouFull => this.addShinryou(shinryouFull, false));
        visitFull.drugs.forEach(drugFull => this.addDrug(drugFull));
        replaceNode(this.map.conductMenuPlaceholder,
            (new ConductMenu(this.prop, this.map.conductWorkarea, this.map.conductWrapper, this.visitId)).ele);
        visitFull.conducts.forEach(conductFull => this.addConduct(conductFull));
        this.map.enterText.addEventListener("click", event => this.doEnterText());
        this.map.sendShohousenFax.addEventListener("click", event => this.doSendShohousenFax());
        this.map.shinryouMenu.addEventListener("click", async event => await this.doRegularShinryou());
        this.ele.addEventListener("temp-visit-changed", event => {
            if (this.prop.tempVisitId === this.getVisitId()) {
                this.ele.classList.add("temp-visit");
            } else {
                this.ele.classList.remove("temp-visit");
            }
        });
        this.ele.addEventListener("text-entered", event => this.addText(event.detail));
        this.ele.addEventListener("shinryou-entered", event => {
            event.detail.forEach(shinryouFull => this.addShinryou(shinryouFull, true));
        });
        this.ele.addEventListener("batch-entered", async event => {
            event.stopPropagation();
            await this.batchEnter(event.detail);
        });
        this.ele.addEventListener("shinryou-entered", event => {
            event.stopPropagation();
            this.addShinryou(event.detail, true);
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
        sendFax.ele.addEventListener("sent", event => {
            let faxSid = event.detail;
            this.ele.dispatchEvent(new CustomEvent("fax-sent", {
                bubbles: true,
                detail: {
                    textId: textId,
                    faxNumber: faxNumber,
                    pdfFile: pdfFilePath,
                    faxSid: faxSid
                }
            }));
            sendFax.ele.remove();
        });
        this.map.commandWrapper.parentElement.insertBefore(sendFax.ele, this.map.commandWrapper);
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
        if (result.shinryouIds.length > 0) {
            let shinryouFullList = await this.rest.listShinryouFullByIds(result.shinryouIds);
            shinryouFullList.forEach(sf => this.addShinryou(sf, true));
        }
        if (result.drugIds.length > 0) {
            let drugFullList = await this.rest.listDrugFullByIds(result.drugIds);
            drugFullList.forEach(drugFull => this.addDrug(drugFull));
        }
        if (result.conductIds.length > 0) {
            let conductFullList = await this.rest.listConductFullByIds(result.conductIds);
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


class RecordOrig extends Component {
    constructor(prop, ele, map) {
        super(ele, map, prop.rest);
        this.prop = prop;
        this.ele.data("component", this);
        this.titleElement = map.title;
        this.leftCommandWrapperElement = map.left.commandWrapper;
        this.enterTextElement = map.left.enterText;
        this.sendShohousenFaxElement = map.left.sendShohousenFax;
        this.textWrapperElement = map.left.textWrapper;
        this.rightElement = map.right_;
        this.hokenWrapperElement = map.right.hokenWrapper;
        this.drugMarkElement = map.right.drugMark;
        this.drugWrapperElement = map.right.drugWrapper;
        this.shinryouMenuElement = map.right.shinryouMenu;
        this.shinryouAuxMenuMap = map.right.shinryouAuxMenu;
        this.shinryouWidgetWorkareaElement = map.right.shinryouWidgetWorkarea;
        this.shinryouWrapperElement = map.right.shinryouWrapper;
        this.conductMenuElement = map.right.conductMenu;
        this.conductWrapperElement = map.right.conductWrapper;
        this.chargeWrapperElement = map.right.chargeWrapper;
        this.ele.get(0).addEventListener("update-payment", event => {
            let payment = event.detail;
            this.updatePaymentState(payment);
        });
        this.ele.get(0).addEventListener("update-0410-no-pay", event => {
            this.update0410NoPay();
        });
    }

    init(visitFull, hokenRep,
         //titleFactory,
         textFactory, hokenFactory, shinryouFactory,
         textEnterFactory, shinryouRegularDialogFactory, conductDispFactory,
         drugDispFactory, sendFaxFactory,
         //chargeFactory,
         currentVisitManager) {
        this.ele.get(0).classList.add(`record-${visitFull.visit.visitId}`);
        this.visitFull = visitFull;
        this.textFactory = textFactory;
        this.hokenFactory = hokenFactory;
        this.shinryouFactory = shinryouFactory;
        this.titleComponent = new Title(this.prop, visitFull.visit);
        this.titleElement.get(0).append(this.titleComponent.ele);
        //this.titleComponent = titleFactory.create(visitFull.visit).appendTo(this.titleElement);
        this.conductDispFactory = conductDispFactory;
        this.drugDispFactory = drugDispFactory;
        this.sendFaxFactory = sendFaxFactory;
        this.currentVisitManager = currentVisitManager;
        this.drugCount = 0;
        visitFull.texts.forEach(text => {
            this.addText(text);
        });
        this.enterTextElement.on("click", event => {
            let comp = textEnterFactory.create(this.visitFull.visit.visitId);
            comp.onEntered((event, entered) => {
                comp.remove();
                this.addText(entered);
            });
            comp.onCancel(event => comp.remove());
            comp.putBefore(this.enterTextElement);
        });
        this.sendShohousenFaxElement.on("click", event => this.doSendShohousenFax());
        this.createHokenComponent(visitFull.hoken, hokenRep).appendTo(this.hokenWrapperElement);
        this.shinryouMenuElement.on("click", async event => {
            if (!this.confirmEdit("診療行為を入力しますか？")) {
                return;
            }
            let result = await shinryouRegularDialogFactory.create(visitFull.visit.visitId).open();
            if (result.mode === "entered") {
                if (result.shinryouIds.length > 0) {
                    let shinryouFullList = await this.rest.listShinryouFullByIds(result.shinryouIds);
                    shinryouFullList.forEach(sf => this.addShinryou(sf, true));
                }
                if (result.drugIds.length > 0) {
                    let drugFullList = await this.rest.listDrugFullByIds(result.drugIds);
                    drugFullList.forEach(drugFull => this.addDrug(drugFull));
                }
                if (result.conductIds.length > 0) {
                    let conductFullList = await this.rest.listConductFullByIds(result.conductIds);
                    conductFullList.forEach(conductFull => this.addConduct(conductFull));
                }
            }
        });
        visitFull.drugs.forEach(drugFull => this.addDrug(drugFull));
        visitFull.shinryouList.forEach(shinryouFull => this.addShinryou(shinryouFull, false));
        visitFull.conducts.forEach(cfull => this.addConduct(cfull));
        let charge = this.chargeComponent = new Charge(this.rest, visitFull.charge, visitFull.visit);
        this.chargeWrapperElement.get(0).append(charge.ele);
        // let compCharge = chargeFactory.create(visitFull.charge);
        // compCharge.appendTo(this.chargeWrapperElement);
        this.shinryouAuxMenuMap.kensa.on("click", async event => await this.doKensa());
        this.shinryouAuxMenuMap.searchEnter.on("click", event => this.doSearchEnter());
        this.shinryouAuxMenuMap.copyAll.on("click", async event => await this.doCopyAll());
    }

    updatePaymentState(payment) {
        this.chargeComponent.updatePaymentState(payment);
    }

    update0410NoPay() {
        this.chargeComponent.update0410NoPay();
    }

    getVisitId() {
        return this.visitFull.visit.visitId;
    }

    getVisitedAt() {
        return this.visitFull.visit.visitedAt;
    }

    createHokenComponent(hoken, hokenRep) {
        let visit = this.visitFull.visit;
        let hokenComp = this.hokenFactory.create(visit.patientId, visit.visitedAt.substring(0, 10),
            visit.visitId, hoken, hokenRep);
        hokenComp.onChanged((hoken, hokenRep) => {
            this.createHokenComponent(hoken, hokenRep).replace(hokenComp);
        });
        return hokenComp;
    }

    onShinryouCopied(cb) {
        this.on("shinryou-copied", (event, targetVisitId, shinryouList) => cb(targetVisitId, shinryouList));
    }

    confirmEdit(msg) {
        let visitId = this.getVisitId();
        if (visitId === this.currentVisitManager.getCurrentVisitId()) {
            return true;
        }
        if (visitId === this.currentVisitManager.getTempVisitId()) {
            return true;
        }
        return confirm("現在診察中でありませんが、" + msg);
    }

    async doKensa() {
        if (!this.confirmEdit("検査を入力しますか？")) {
            return;
        }
        let dialog = new ShinryouKensaDialog(this.getVisitId(), this.rest);
        dialog.setTitle("検査入力");
        let result = await dialog.open();
        if (result) {
            for (let shinryouId of result.shinryouIds) {
                let s = await this.rest.getShinryouFull(shinryouId);
                this.addShinryou(s, true);
            }
            for (let drugId of result.drugIds) {
                let d = await this.rest.getDrugFull(drugId);
                this.addDrug(d);
            }
            for (let conductId of result.conductIds) {
                let c = await this.rest.getConductFull(conductId);
                this.addConduct(c);
            }
        }
    }

    doSearchEnter() {
        if (!this.confirmEdit("診療行為を入力しますか？")) {
            return;
        }
        let widget = shinryouSearchEnterWidgetFactory.create(this.getVisitId(), this.getVisitedAt(), this.rest);
        widget.onEntered(entered => this.addShinryou(entered));
        widget.prependTo(this.shinryouWidgetWorkareaElement);
        widget.focus();
    }

    async doCopyAll() {
        let targetVisitId = this.currentVisitManager.resolveCopyTarget();
        if (targetVisitId === 0) {
            alert("ｺﾋﾟｰ先を見つけられません。");
            return;
        }
        let shinryouList = await this.rest.listShinryou(this.getVisitId());
        let newShinryouIds = await this.rest.batchCopyShinryou(targetVisitId, shinryouList);
        let shinryouFulls = await this.rest.listShinryouFullByIds(newShinryouIds);
        this.trigger("shinryou-copied", [targetVisitId, shinryouFulls]);
    }

    getPharmaTextRegex() {
        return /(.+)にファックス（(\+\d+)）で送付/;
    }

    async doSendShohousenFax() {
        let shohousenText;
        let pharmaName;
        let faxNumber;
        let textComponents = this.textFactory.listTextComponents(this.textWrapperElement);
        let pharmaRegex = this.getPharmaTextRegex();
        for (let textComp of textComponents) {
            let content = textComp.getText().content;
            if (content.startsWith("院外処方")) {
                shohousenText = textComp;
                continue;
            }
            let matches = pharmaRegex.exec(content);
            if (matches) {
                pharmaName = matches[1];
                faxNumber = matches[2];
            }
        }
        if (!shohousenText) {
            alert("処方箋の入力を見つけられません。");
            return;
        }
        let textId = shohousenText.getText().textId;
        let date = this.visitFull.visit.visitedAt.substring(0, 10);
        let pdfFilePath = await this.rest.probeShohousenFaxImage(textId, date);
        if (!pdfFilePath) {
            if (!confirm("送信用の処方箋イメージを作成しますか？")) {
                return;
            }
            pdfFilePath = await createShohousenPdfForFax(shohousenText.getText(), this.rest);
        }
        let compSendFax = this.sendFaxFactory.create(pdfFilePath, faxNumber);
        compSendFax.onSent((event, faxSid) => {
            this.trigger("fax-sent", {
                textId: textId,
                faxNumber: faxNumber,
                pdfFile: pdfFilePath,
                faxSid: faxSid
            });
        });
        compSendFax.putBefore(this.leftCommandWrapperElement);
    }

    onFaxSent(cb) {
        this.on("fax-sent", (event, data) => cb(event, data));
    }

    showDrugMark() {
        this.drugMarkElement.removeClass("d-none");
    }

    hideDrugMark() {
        this.drugMarkElement.addClass("d-none");
    }

    addDrug(drugFull) {
        let compDrug = this.drugDispFactory.create(drugFull);
        compDrug.setIndex(++this.drugCount);
        compDrug.appendTo(this.drugWrapperElement);
        this.showDrugMark();
    }

    addConduct(conductFull) {
        let compConduct = this.conductDispFactory.create(conductFull);
        compConduct.appendTo(this.conductWrapperElement);
    }

    addShinryou(shinryouFull, searchLocation = true) {
        let compShinryou = this.shinryouFactory.create(shinryouFull);
        compShinryou.onDeleted(event => compShinryou.remove());
        if (searchLocation) {
            let shinryoucode = shinryouFull.shinryou.shinryoucode;
            let xs = this.shinryouWrapperElement.find(".practice-shinryou");
            let found = false;
            for (let i = 0; i < xs.length; i++) {
                let x = xs.slice(i, i + 1);
                let c = x.data("component");
                let code = c.getShinryoucode();
                if (shinryoucode < code) {
                    compShinryou.putBefore(x);
                    found = true;
                    break;
                }
            }
            if (!found) {
                compShinryou.appendTo(this.shinryouWrapperElement);
            }
        } else {
            compShinryou.appendTo(this.shinryouWrapperElement);
        }
    }

    createTextComponent(text) {
        let compText = this.textFactory.create(text);
        compText.onUpdated((event, updatedText) => {
            let compUpdated = this.createTextComponent(updatedText);
            compUpdated.replace(compText.ele);
        });
        compText.onDeleted(event => compText.remove());
        compText.onCopied((event, copiedText) => this.trigger("text-copied", copiedText));
        return compText;
    }

    addText(text) {
        let compText = this.createTextComponent(text);
        compText.appendTo(this.textWrapperElement);
    }

    onTextCopied(cb) {
        this.on("text-copied", (event, copiedText) => cb(event, copiedText));
    }

    // getVisitId() {
    //     return this.visitFull.visit.visitId;
    // }

    markAsCurrent() {
        this.ele.addClass("current-visit");
        //this.titleComponent.markAsCurrent();
    }

    markAsTemp() {
        this.ele.addClass("temp-visit");
        //this.titleComponent.markAsTemp();
    }

    clearMark() {
        this.ele.removeClass("temp-visit");
        //this.titleComponent.clearMark();
    }

    onDelete(cb) {
        this.titleComponent.onDelete(cb);
    }

    onTempVisit(cb) {
        this.titleComponent.onTempVisit(cb);
    }

    onClearTempVisit(cb) {
        this.titleComponent.onClearTempVisit(cb);
    }
}