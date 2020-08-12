import {parseElement} from "../../js/parse-element.js";

let html = `
<textarea class="x-textarea"></textarea>
<div class="commands">
    <a href="javascript:void(0)" class="x-enter">入力</a> <span class="sep">|</span>
    <a href="javascript:void(0)" class="x-cancel">キャンセル</a>
</div>
`;

export function createTextEnter(visitId, rest){
    let ele = document.createElement("div");
    ele.classList.add("text-enter");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.enter.onclick = event => doEnter(ele, map, visitId, rest);
    map.cancel.onclick = event => ele.dispatchEvent(new Event("cancel", {bubbles: true}));
    return ele;
}

async function doEnter(ele, map, visitId, rest){
    let text = {
        visitId: visitId,
        content: map.textarea.value
    };
    let textId = await rest.enterText(text);
    let entered = await rest.getText(textId);
    ele.dispatchEvent(new CustomEvent("text-entered", {bubbles: true, detail: entered}));
}
