import {Component} from "./component.js";

export class Record extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.ele.data("component", this);
        this.titleElement = map.title;
        this.enterTextElement = map.left.enterText;
        this.sendShohousenFaxElement = map.left.sendShohousenFax;
        this.textWrapperElement = map.left.textWrapper;
        this.rightElement = map.right_;
        this.hokenWrapperElement = map.right.hokenWrapper;
        this.drugMarkElement = map.right.drugMark;
        this.drugWrapperElement = map.right.drugWrapper;
        this.shinryouMenuElement = map.right.shinryouMenu;
        this.shinryouWrapperElement = map.right.shinryouWrapper;
        this.conductMenuElement = map.right.conductMenu;
        this.conductWrapperElement = map.right.conductWrapper;
    }

    init(visitFull, hokenRep, titleFactory, textFactory, hokenFactory, shinryouFactory,
         textEnterFactory, shinryouRegularDialogFactory, conductDispFactory,
         drugDispFactory) {
        this.visitFull = visitFull;
        this.textFactory = textFactory;
        this.shinryouFactory = shinryouFactory;
        this.titleComponent = titleFactory.create(visitFull.visit).appendTo(this.titleElement);
        this.conductDispFactory = conductDispFactory;
        this.drugDispFactory = drugDispFactory;
        this.drugCount = 0;
        visitFull.texts.forEach(text => {
            this.addText(text);
        });
        this.enterTextElement.on("click", event => {
            let comp = textEnterFactory.create(this.visitFull.visit.visitId);
            comp.onEntered((event, entered) => {
                comp.remove();
                textFactory.create(entered).appendTo(this.textWrapperElement);
            });
            comp.onCancel(event => comp.remove());
            comp.putBefore(this.enterTextElement);
        });
        this.sendShohousenFaxElement.on("click", event => this.doSendShohousenFax());
        hokenFactory.create(hokenRep).appendTo(this.hokenWrapperElement);
        this.shinryouMenuElement.on("click", async event => {
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
    }

    getPharmaTextRegex(){
        return /(.+)にファックス（(\+\d+)）で送付/;
    }

    doSendShohousenFax(){
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
        console.log(shohousenText);
        console.log(pharmaName);
        console.log(faxNumber);
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

    getVisitId() {
        return this.visitFull.visit.visitId;
    }

    markAsCurrent() {
        this.titleComponent.markAsCurrent();
    }

    markAsTemp() {
        this.titleComponent.markAsTemp();
    }

    clearMark() {
        this.titleComponent.clearMark();
    }

    onDeleted(cb) {
        this.titleComponent.onDeleted(cb);
    }

    onTempVisit(cb) {
        this.titleComponent.onTempVisit(cb);
    }

    onClearTempVisit(cb) {
        this.titleComponent.onClearTempVisit(cb);
    }
}