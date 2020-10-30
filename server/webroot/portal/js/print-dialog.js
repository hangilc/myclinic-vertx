import {modalOpen} from "./modal-dialog.js";
import {parseElement} from "./parse-node.js";

let tmpl = `
    <h3>印刷</h3>
    <div>
        <div class="x-doc"></div>
        <div>
            設定：<select class="x-select"><option value="--manual--">手動</option></select>
        </div>
    </div>
    <div class="command-box">
    <button class="x-print">印刷</button>
    <button class="x-cancel">キャンセル</button>
    </div>
`;

export async function openPrintDialog(docName, req, settings, rest){
    let ele = document.createElement("div");
    ele.innerHTML = tmpl;
    let map = parseElement(ele);
    map.doc.innerText = docName;
    let url = "http://127.0.0.1:48080/print";
    return modalOpen(ele, close => {
        map.print.addEventListener("click", event => {
            let setting = map.select.value;
            let xhr = new XMLHttpRequest();
            xhr.open("POST", url);
            xhr.send(JSON.stringify(req));
            close(true);
        });
        map.cancel.addEventListener("click", event => close(false));
    });
}
