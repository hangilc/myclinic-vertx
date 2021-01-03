import {Component} from "../component.js";
import {formatPresc} from "../../js/format-presc.js";
import {RegisteredDrugDialog} from "../registered-drug-dialog/registered-drug-dialog.js";
import {
    createShohousenOps,
    createShohousenPdfForFax,
    shohousenTextContentDataToDisp,
    shohousenTextContentDispToData
} from "../funs.js";
import {ShohousenPreviewDialog} from "../shohousen-preview-dialog.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {show, hide} from "../../../js/dom-helper.js";

let tmpl = `
    <div class="mt-2">
        <textarea class="form-control x-textarea" rows="6"></textarea>
        <div class="form-inline mt-2">
            <a href="javascript:void(0)" class="x-enter">入力</a>
            <a href="javascript:void(0)" class="x-cancel ml-2">キャンセル</a>
            <a href="javascript:void(0)" class="x-copy-memo d-none ml-2">引継ぎコピー</a>
            <a href="javascript:void(0)" class="x-delete ml-2">削除</a>
            <div class="dropbox x-shohousen-menu d-none">
                <button type="button" class="btn btn-link dropdown-toggle"
                        data-toggle="dropdown">処方箋</button>
                <div class="dropdown-menu">
                    <a href="javascript:void(0)" class="x-shohousen dropdown-item">処方箋発行</a>
                    <a href="javascript:void(0)" class="x-shohousen-fax dropdown-item">処方箋FAX</a>
                    <a href="javascript:void(0)" class="x-format-presc dropdown-item">処方箋整形</a>
                    <a href="javascript:void(0)" class="x-preview-current dropdown-item">編集中表示</a>
                </div>
            </div>
            <a href="javascript:void(0)" class="x-copy ml-2">コピー</a>
        </div>
    </div>
`;

export class TextEdit {
    constructor(prop, text){
        this.prop = prop;
        this.text = text;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.textarea.value = shohousenTextContentDataToDisp(text.content);
        this.map.enter.addEventListener("click", async event => await this.doEnter());
        this.map.cancel.addEventListener("click", event => this.ele.dispatchEvent(new Event("cancel")));
        this.map.delete.addEventListener("click", async event => await this.doDelete());
        this.map.copy.addEventListener("click", async event => await this.doCopy());
        if (hasMemo(text.content)) {
            this.map.copyMemo.addEventListener("click", async event => await this.doCopyMemo());
            show(this.map.copyMemo);
        }
        if( isShohousen(text.content) ){
            this.map.shohousen.addEventListener("click", async event => await this.doShohousen());
            this.map.shohousenFax.addEventListener("click", async event => await this.doShohousenFax());
            this.map.formatPresc.addEventListener("click", async event => await this.doFormatPresc());
            this.map.previewCurrent.addEventListener("click", async event => await this.doPreviewCurrent());
            show(this.map.shohousenMenu);
        }
    }

    initFocus(){
        this.map.textarea.focus();
    }

    async doEnter(){
        let content = shohousenTextContentDispToData(this.map.textarea.value.trim());
        let text = Object.assign({}, this.text, {content: content});
        await this.prop.rest.updateText(text);
        let updatedText = await this.prop.rest.getText(text.textId);
        this.ele.dispatchEvent(new CustomEvent("updated", {detail: updatedText}));
    }

    async doDelete() {
        if (confirm("本当にこの文章を削除していいですか？")) {
            let textId = this.text.textId;
            await this.prop.rest.deleteText(textId);
            this.ele.dispatchEvent(new Event("deleted"));
        }
    }

    async doCopyMemo() {
        let targetVisitId = this.prop.getTargetVisitId();
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
            let textId = await this.prop.rest.enterText(t);
            let copied = await this.prop.rest.getText(textId);
            this.prop.publishTextEntered(copied);
            this.ele.dispatchEvent(new Event("cancel"));
        }
    }

    async doCopy() {
        let targetVisitId = this.prop.getTargetVisitId();
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
        let textId = await this.prop.rest.enterText(t);
        let copied = await this.prop.rest.getText(textId);
        this.prop.publishTextEntered(copied);
        this.ele.dispatchEvent(new Event("cancel"));
    }

    async doShohousen() {
        let ops = await createShohousenOps(this.text, {}, this.prop.rest);
        let dialog = new ShohousenPreviewDialog(ops);
        await dialog.setPrintAPI(this.prop.printAPI);
        await dialog.open();
        this.ele.dispatchEvent(new Event("cancel"));
    }

    async doShohousenFax() {
        if (confirm("この処方箋をPDFとして保存しますか？")) {
            await createShohousenPdfForFax(this.text, this.prop.rest);
            this.ele.dispatchEvent(new Event("cancel"));
        }
    }

    async doFormatPresc() {
        let src = this.map.textarea.value;
        src = src.replace(/\s*$/, "");
        src = shohousenTextContentDispToData(src);
        let dst = formatPresc(src);
        dst = shohousenTextContentDataToDisp(dst);
        this.map.textarea.value = dst;
    }

    async doPreviewCurrent() {
        let content = this.map.textarea.value;
        content = shohousenTextContentDispToData(content);
        let tmpText = Object.assign({}, this.text, {content: content});
        let ops = await createShohousenOps(tmpText, {}, this.prop.rest);
        let dialog = new ShohousenPreviewDialog(ops);
        await dialog.setPrintAPI(this.prop.printAPI);
        await dialog.open();
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

function hasMemo(content) {
    return content && (content.startsWith("●") || content.startsWith("★"));
}

function isShohousen(content){
    return content.startsWith("院外処方");
}

