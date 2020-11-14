import {Dialog} from "./dialog.js";
import {parseElement} from "../js/parse-node.js";
import {PrintAPI} from "../../js/print-api.js";

let tmpl = `
    <div>
        <div class="x-doc"></div>
        <div>
            設定：<select class="x-select"><option value="--manual--">手動</option></select>
        </div>
    </div>
    <div>
        <a href="http://127.0.0.1:48080/" target="_blank">管理画面表示</a>
    </div>
    <div class="command-box">
    <button class="x-print">印刷</button>
    <button class="x-cancel">キャンセル</button>
    </div>
`;

export class PrintDialog extends Dialog {
    constructor(){
        super();
    }
}

async function openPrintDialog(docName, setupOps, pagesOps, prog, kind){
    let ele = document.createElement("div");
    ele.innerHTML = tmpl;
    let map = parseElement(ele);
    map.doc.innerText = docName;
    let api = new PrintAPI("http://127.0.0.1:48080");
    let pref = await api.getPref(prog, kind);
    let settings = await api.listSetting();
    for(let setting of settings){
        let opt = document.createElement("option");
        opt.innerText = setting;
        if( pref && pref === setting ){
            opt.selected = true;
        }
        map.select.appendChild(opt);
    }
    return modalOpen(ele, close => {
        map.print.addEventListener("click", async event => {
            let setting = map.select.value;
            if( setting === "--manual--" ){
                await api.print(setupOps, pagesOps);
                await api.deletePref(prog, kind);
                close(true);
            } else if( setting ){
                await api.print(setupOps, pagesOps, setting);
                await api.setPref(prog, kind, setting);
                close(true);
            } else {
                alert("Invalid settting: " + setting);
            }
        });
        map.cancel.addEventListener("click", event => close(false));
    });
}
