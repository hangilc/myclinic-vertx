import {modalOpen} from "../../comp/modal-dialog.js";
import {parseElement} from "../../js/parse-element.js";
import {createDrawerPreview} from "../../comp/drawer-preview.js";

let html = `
<h3>処方箋</h3>
<div class="x-disp disp"></div>
<div class="command-box">
    <button>印刷</button>
    <button class="x-close">閉じる</button>
</div>
`;
export async function openShohousenPreviewDialog(ops){
    let ele = document.createElement("div");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.disp.append(createDrawerPreview(ops, 1.0));
    return await modalOpen(ele, close => {
        map.close.onclick = event => close();
    });
}