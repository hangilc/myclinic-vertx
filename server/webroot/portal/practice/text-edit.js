import {Component} from "./component.js";
import {formatPresc} from "../js/format-presc.js";

;

export class TextEdit extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.textareaElement = map.textarea;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
        this.copyMemoElement = map.copyMemo;
        this.deleteElement = map.delete;
        this.shohousenMenuElement = map.shohousenMenu;
        this.shohousenElement = map.shohousen;
        this.shohousenFaxElement = map.shohousenFax;
        this.formatPrescElement = map.formatPresc;
        this.previewCurrentElement = map.previewCurrent;
        this.copyElement = map.copy;
    }

    init(text, currentVisitManager, shohousenPreviewDialogFactory) {
        this.text = text;
        this.currentVisitManager = currentVisitManager;
        this.shohousenPreviewFactory = shohousenPreviewDialogFactory;
        this.textareaElement.val(this.contentFromData(text.content));
        this.enterElement.on("click", event => this.doEnter());
        this.cancelElement.on("click", event => this.ele.trigger("cancel"));
        if( hasMemo(text.content) ){
            this.copyMemoElement.on("click", event => this.doCopyMemo());
            this.copyMemoElement.removeClass("d-none");
        }
        this.deleteElement.on("click", event => this.doDelete());
        this.copyElement.on("click", event => this.doCopy());
        if (text.content.startsWith("院外処方")) {
            this.shohousenElement.on("click", event => this.doShohousen());
            this.shohousenFaxElement.on("click", event => this.doShohousenFax());
            this.formatPrescElement.on("click", event => this.doFormatPresc());
            this.previewCurrentElement.on("click", event => this.doPreviewCurrent());
            this.shohousenMenuElement.removeClass("d-none").addClass("d-inline");
        }
    }

    initFocus() {
        this.textareaElement.focus();
    }

    contentFromData(content) {
        if (content.startsWith("院外処方")) {
            return content.replace(/\u{3000}/ug, " "); // replace zenkaku space to ascii space
        } else {
            return content;
        }
    }

    asContentOfData(content){
        if (content.startsWith("院外処方")) {
            return content.replace(/ /ug, "　"); // replace ascii space to zenkaku space
        } else {
            return content;
        }
    }

    async createShohousenOps(content, reqOpts) {
        let visit = await this.rest.getVisit(this.text.visitId);
        let visitDate = visit.visitedAt.substring(0, 10);
        let req = {};
        req.clinicInfo = await this.rest.getClinicInfo();
        req.hoken = await this.rest.getHoken(this.text.visitId);
        req.patient = await this.rest.getPatient(visit.patientId);
        let rcptAge = await this.rest.calcRcptAge(req.patient.birthday, visitDate);
        req.futanWari = await this.rest.calcFutanWari(req.hoken, rcptAge);
        req.issueDate = visitDate;
        req.drugs = content;
        Object.assign(req, reqOpts);
        return await this.rest.shohousenDrawer(req);
    }

    async doPreviewCurrent() {
        let content = this.textareaElement.val();
        content = this.asContentOfData(content);
        let ops = await this.createShohousenOps(content);
        let dialog = this.shohousenPreviewFactory.create(ops);
        await dialog.open();
    }

    async doFormatPresc() {
        let src = this.textareaElement.val();
        src = src.replace(/\s*$/, "");
        src = this.asContentOfData(src);
        let dst = formatPresc(src);
        dst = this.contentFromData(dst);
        this.textareaElement.val(dst);
    }

    async doShohousen() {
        let ops = await this.createShohousenOps(this.text.content);
        let dialog = this.shohousenPreviewFactory.create(ops);
        await dialog.open();
        this.ele.trigger("cancel");
    }

    async doShohousenFax() {
        if (confirm("この処方箋をPDFとして保存しますか？")) {
            let visit = await this.rest.getVisit(this.text.visitId);
            let patient = await this.rest.getPatient(visit.patientId);
            let name = await this.rest.convertToRomaji(patient.lastNameYomi + patient.firstNameYomi);
            let savePath = await this.rest.getShohousenSavePdfPath(name, this.text.textId,
                patient.patientId, visit.visitedAt.substring(0, 10));
            let stampInfo = await this.rest.shohousenGrayStampInfo();
            let content = this.text.content;
            content = this.asContentOfData(content);
            let ops = await this.createShohousenOps(content, {color: "black"});
            await this.rest.saveDrawerAsPdf([ops], "A5", savePath, {stamp: stampInfo});
            this.ele.trigger("cancel");
        }
    }

    onCopied(cb) {
        this.on("copied", (event, text) => cb(event, text));
    }

    async doCopy() {
        let targetVisitId = this.currentVisitManager.resolveCopyTarget();
        if (targetVisitId === 0) {
            alert("コピー先が設定されていません。");
            return;
        }
        if (targetVisitId === this.text.visitId) {
            alert("同じ診療記録にはコピーできません。");
            return;
        }
        let t = Object.assign({}, this.text);
        t.textId = 0;
        t.visitId = targetVisitId;
        let textId = await this.rest.enterText(t);
        let copied = await this.rest.getText(textId);
        this.trigger("copied", copied);
    }

    async doCopyMemo() {
        let targetVisitId = this.currentVisitManager.resolveCopyTarget();
        if (targetVisitId === 0) {
            alert("コピー先が設定されていません。");
            return;
        }
        if (targetVisitId === this.text.visitId) {
            alert("同じ診療記録にはコピーできません。");
            return;
        }
        let memo = extractMemo(this.text.content);
        if (memo) {
            let t = {
                textId: 0,
                visitId: targetVisitId,
                content: memo
            }
            let textId = await this.rest.enterText(t);
            let copied = await this.rest.getText(textId);
            this.trigger("copied", copied);
        }
    }

    async doEnter() {
        let content = this.textareaElement.val();
        content = this.asContentOfData(content);
        let text = Object.assign({}, this.text, {content: content});
        await this.rest.updateText(text);
        let updatedText = await this.rest.getText(text.textId);
        this.ele.trigger("updated", updatedText);
    }

    async doDelete() {
        if (confirm("本当にこの文章を削除していいですか？")) {
            let textId = this.text.textId;
            await this.rest.deleteText(textId);
            this.ele.trigger("deleted");
        }
    }

    onDeleted(cb) {
        this.ele.on("deleted", cb);
    }

    onUpdated(cb) {
        this.ele.on("updated", cb);
    }

    onCancel(cb) {
        this.ele.on("cancel", cb);
    }
}

let lineTermPattern = /\r\n|\n|\r/;

function extractMemo(content) {
    let lines = content.split(lineTermPattern);
    let memo = [];
    for (let line of lines) {
        if (line.startsWith("●") || line.startsWith("★")) {
            memo.push(line);
        } else {
            break;
        }
    }
    return memo.join("\n");
}

export function hasMemo(content){
    return content && (content.startsWith("●") || content.startsWith("★") );
}