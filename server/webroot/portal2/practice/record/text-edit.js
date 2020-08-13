import {parseElement} from "../../js/parse-element.js";
import {createDropdown} from "../../comp/dropdown.js";
import * as F from "../functions.js";
import {openShohousenPreviewDialog} from "./shohousen-preview-dialog.js";

let html = `
<textarea class="x-textarea"></textarea>
<div class="commands">
    <a href="javascript:void(0)" class="x-enter">入力</a> <span class="sep">|</span>
    <a href="javascript:void(0)" class="x-cancel">キャンセル</a> <span class="sep">|</span>
    <a href="javascript:void(0)" class="x-copy-memo">引継ぎコピー</a> <span class="sep">|</span>
    <a href="javascript:void(0)" class="x-delete">削除</a> <span class="sep">|</span>
    <a href="javascript:void(0)" class="x-shohousen">処方箋▼</a> <span class="sep">|</span>
    <a href="javascript:void(0)" class="x-copy">コピー</a>
</div>
`;


export function createTextEdit(text, rest) {
    let ele = document.createElement("div");
    ele.classList.add("text-edit");
    ele.innerHTML = html;
    let map = parseElement(ele);
    if (!hasMemo(text.content)) {
        map.copyMemo.classList.add("hidden");
    }
    if (!hasShohousen(text.content)) {
        map.shohousen.classList.add("hidden");
    } else {
        setupShohousenDropdown(map.shohousen, text, rest);
    }
    map.textarea.value = text.content;
    map.enter.onclick = async event => {
        let t = Object.assign({}, text, {content: map.textarea.value});
        await rest.updateText(t);
        ele.dispatchEvent(new CustomEvent(
            "text-updated",
            {
                bubbles: true,
                detail: t
            }
        ));
    };
    map.cancel.onclick = event => ele.dispatchEvent(new Event("do-edit-cancel", {bubbles: true}));
    map.copyMemo.onclick = event => ele.dispatchEvent(F.event("do-text-copy-memo",
        {srcText: text, onSuccess: () => ele.remove()}));
    map.delete.onclick = event => doDelete(text.textId, ele, rest);
    map.copy.onclick = event => ele.dispatchEvent(F.event("do-text-copy",
        {srcText: text, onSuccess: () => ele.remove()}));
    return ele;
}

async function doDelete(textId, ele, rest){
    if( !confirm("この文章を削除していいですか？") ){
        return;
    }
    await rest.deleteText(textId);
    ele.dispatchEvent(new CustomEvent("text-deleted", {bubbles: true, detail: textId}));
}

function hasMemo(content) {
    return content && (content.startsWith("●") || content.startsWith("★"));
}

function hasShohousen(content) {
    return content.startsWith("院外処方");
}

function setupShohousenDropdown(link, text, rest) {
    createDropdown(link, [
        {
            label: "処方箋発行",
            action: async () => {
                let ops = await F.createShohousenOps(text.content, text.visitId, rest);
                await openShohousenPreviewDialog(ops);
            }
        },
        {
            label: "処方箋FAX",
            action: () => {
            }
        }, {
            label: "登録薬剤",
            action: () => {
            }
        },
        {
            label: "処方箋整形",
            action: () => {
            }
        },
        {
            label: "編集中表示",
            action: () => {
            }
        }
    ]);
}

