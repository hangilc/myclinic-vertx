import {Component} from "./component.js";
import {shinryouSearchEnterWidgetFactory} from "./shinryou-search-enter-widget/shinryou-search-enter-widget.js";
import {createShohousenPdfForFax} from "./funs.js";
import {ShinryouKensaDialog} from "./shinryou-kensa-dialog.js";
import {Charge} from "./charge/charge.js";
import {Title} from "./title/title.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {Text} from "./text/text.js";

let tmpl = `
    <div class="practice-record">
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
                    <div class="dropdown">
                        <button type="button" class="btn btn-link dropdown-toggle"
                                data-toggle="dropdown">その他
                        </button>
                        <div class="dropdown-menu x-shinryou-aux-menu_">
                            <a href="javascript:void(0)" class="x-kensa dropdown-item">検査</a>
                            <a href="javascript:void(0)" class="x-search-enter dropdown-item">検索入力</a>
                            <a href="javascript:void(0)" class="x-copy-all dropdown-item">全部コピー</a>
                        </div>
                    </div>
                </div>
                <div class="x-shinryou-widget-workarea"></div>
                <div class="x-shinryou-wrapper"></div>
                <a href="javascript:void(0)" class="x-conduct-menu">［処置］</a>
                <div class="x-conduct-wrapper"></div>
                <div class="x-charge-wrapper"></div>
            </div>
        </div>
    </div>
`;

export class Record {
    constructor(prop, visitFull){
        this.prop = prop;
        this.visitFull = visitFull;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        let title = new Title(prop, visitFull.visit);
        this.map.title.append(title.ele);
        visitFull.texts.forEach(text => {
            let textComponent = new Text(this.prop, text);
            this.map.textWrapper.append(textComponent.ele);
        });
    }
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
            if( !this.confirmEdit("診療行為を入力しますか？") ){
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

    updatePaymentState(payment){
        this.chargeComponent.updatePaymentState(payment);
    }

    update0410NoPay(){
        this.chargeComponent.update0410NoPay();
    }

    getVisitId(){
        return this.visitFull.visit.visitId;
    }

    getVisitedAt(){
        return this.visitFull.visit.visitedAt;
    }

    createHokenComponent(hoken, hokenRep){
        let visit = this.visitFull.visit;
        let hokenComp = this.hokenFactory.create(visit.patientId, visit.visitedAt.substring(0, 10),
            visit.visitId, hoken, hokenRep);
        hokenComp.onChanged((hoken, hokenRep) => {
            this.createHokenComponent(hoken, hokenRep).replace(hokenComp);
        });
        return hokenComp;
    }

    onShinryouCopied(cb){
        this.on("shinryou-copied", (event, targetVisitId, shinryouList) => cb(targetVisitId, shinryouList));
    }

    confirmEdit(msg){
        let visitId = this.getVisitId();
        if( visitId === this.currentVisitManager.getCurrentVisitId() ){
            return true;
        }
        if( visitId === this.currentVisitManager.getTempVisitId() ){
            return true;
        }
        return confirm("現在診察中でありませんが、" + msg);
    }

    async doKensa(){
        if( !this.confirmEdit("検査を入力しますか？") ){
            return;
        }
        let dialog = new ShinryouKensaDialog(this.getVisitId(), this.rest);
        dialog.setTitle("検査入力");
        let result = await dialog.open();
        if( result ){
            for(let shinryouId of result.shinryouIds){
                let s = await this.rest.getShinryouFull(shinryouId);
                this.addShinryou(s, true);
            }
            for(let drugId of result.drugIds){
                let d = await this.rest.getDrugFull(drugId);
                this.addDrug(d);
            }
            for(let conductId of result.conductIds){
                let c = await this.rest.getConductFull(conductId);
                this.addConduct(c);
            }
        }
    }

    doSearchEnter(){
        if( !this.confirmEdit("診療行為を入力しますか？") ){
            return;
        }
        let widget = shinryouSearchEnterWidgetFactory.create(this.getVisitId(), this.getVisitedAt(), this.rest);
        widget.onEntered(entered => this.addShinryou(entered));
        widget.prependTo(this.shinryouWidgetWorkareaElement);
        widget.focus();
    }

    async doCopyAll(){
        let targetVisitId = this.currentVisitManager.resolveCopyTarget();
        if( targetVisitId === 0 ){
            alert("ｺﾋﾟｰ先を見つけられません。");
            return;
        }
        let shinryouList = await this.rest.listShinryou(this.getVisitId());
        let newShinryouIds = await this.rest.batchCopyShinryou(targetVisitId, shinryouList);
        let shinryouFulls = await this.rest.listShinryouFullByIds(newShinryouIds);
        this.trigger("shinryou-copied", [targetVisitId, shinryouFulls]);
    }

    getPharmaTextRegex(){
        return /(.+)にファックス（(\+\d+)）で送付/;
    }

    async doSendShohousenFax(){
        let shohousenText;
        let pharmaName;
        let faxNumber;
        let textComponents = this.textFactory.listTextComponents(this.textWrapperElement);
        let pharmaRegex = this.getPharmaTextRegex();
        for(let textComp of textComponents){
            let content = textComp.getText().content;
            if( content.startsWith("院外処方") ){
                shohousenText = textComp;
                continue;
            }
            let matches = pharmaRegex.exec(content);
            if( matches ){
                pharmaName = matches[1];
                faxNumber = matches[2];
            }
        }
        if( !shohousenText ){
            alert("処方箋の入力を見つけられません。");
            return;
        }
        let textId = shohousenText.getText().textId;
        let date = this.visitFull.visit.visitedAt.substring(0, 10);
        let pdfFilePath = await this.rest.probeShohousenFaxImage(textId, date);
        if( !pdfFilePath ){
            if( !confirm("送信用の処方箋イメージを作成しますか？") ){
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

    onFaxSent(cb){
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

    createTextComponent(text){
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

    onTextCopied(cb){
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