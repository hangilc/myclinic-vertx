import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";

let html = `
<h3>診療行為編集</h3>
<div class="x-name"></div>
<div class="command-box">
    <button class="x-delete">削除</button>
    <button class="x-close">閉じる</button>
    <a href="javascript:void(0)" class="x-tekiyou">摘要編集</a>
</div>
`;

export function createShinryouEdit(shinryouFull, rest){
    let ele = document.createElement("div");
    ele.classList.add("workarea");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.name.innerText = shinryouFull.master.name;
    map.delete.onclick = async event => {
        if( !confirm("この診療行為を削除していいですか？") ){
            return;
        }
        let shinryouId = shinryouFull.shinryou.shinryouId;
        await rest.deleteShinryou(shinryouId);
        ele.dispatchEvent(F.event("shinryou-deleted", shinryouId));
    };
    map.close.onclick = event => ele.dispatchEvent(F.event("close"));
    return ele;
}