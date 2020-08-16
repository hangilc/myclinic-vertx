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
    map.name.innerText = F.createShinryouLabel(shinryouFull.master.name, F.getShinryouTekiyou(shinryouFull));
    map.delete.onclick = async event => {
        if( !confirm("この診療行為を削除していいですか？") ){
            return;
        }
        let shinryouId = shinryouFull.shinryou.shinryouId;
        await rest.deleteShinryou(shinryouId);
        ele.dispatchEvent(F.event("shinryou-deleted", shinryouId));
    };
    map.tekiyou.onclick = event => doTekiyou(F.getShinryouTekiyou(shinryouFull),
        shinryouFull.shinryou.shinryouId, ele, rest);
    map.close.onclick = event => ele.dispatchEvent(F.event("close"));
    ele.addEventListener("shinryou-tekiyou-changed", event => {
        let tekiyou = event.detail.tekiyou;
        map.name.innerText = F.createShinryouLabel(shinryouFull.master.name, tekiyou);
    });
    return ele;
}

async function doTekiyou(tekiyou, shinryouId, ele, rest){
    let result = prompt("診療行為の摘要", tekiyou);
    if( result != null ){
        await rest.setShinryouTekiyou(shinryouId, result);
        ele.dispatchEvent(F.event("shinryou-tekiyou-changed", {
            shinryouId: shinryouId,
            tekiyou: result
        }));
    }
}
