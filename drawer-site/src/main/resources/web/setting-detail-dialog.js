import {ModalDialog} from "./modal-dialog.js";
import {parseElement} from "./parse-node.js";

let tmpl = `
<div>名前：<span class="x-name"></span></div>
<div>プリンター: <span class="x-printer"></span></div>
<div>用紙サイズ: <span class="x-paper-size"></span></div>
<div>向き: <span class="x-orientation"></span></div>
<div>トレイ: <span class="x-tray"></span></div>
<div>品質: <span class="x-quality"></span></div>
`;

let commandsTmpl = `
    <button class="x-close">閉じる</button>
`;

export async function showSettingDetailDialog(name, detail){
    let dialog = new ModalDialog("印刷設定の詳細");
    dialog.getContent().innerHTML = tmpl;
    let map = parseElement(dialog.getContent());
    map.name.innerText = name;
    map.printer.innerText = detail.printer;
    map.paperSize.innerText = detail.paperSize;
    map.orientation.innerText = detail.orientation;
    map.tray.innerText = detail.tray;
    map.quality.innerText = detail.quality;
    dialog.getCommands().innerHTML = commandsTmpl;
    let cmap = parseElement(dialog.getCommands());
    cmap.close.addEventListener("click", event => dialog.close());
    return await dialog.open();
}