import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";

let html = `
<div>
    <span class="x-pdf-file"></span>
    <a href="javascript:void(0)" class="x-view">プレビュー</a>
</div>
<div class="x-fax-number"></div>
<div class="x-pharma-name"></div>
<div>
    <button class="x-send">送信</button>
    <a href="javascript:void(0)" class="x-cancel">キャンセル</a>
</div>
`;

export function createSendFax(pdfFile, faxNumber, pharmaName, rest){
    let ele = document.createElement("div");
    ele.classList.add("send-fax");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.pdfFile.innerText = getFilePart(pdfFile);
    map.faxNumber.innerText = faxNumber;
    map.pharmaName.innerText = pharmaName;
    map.view.onclick = event => doView(pdfFile, rest);
    map.send.onclick = async event => {
        let faxSid = await F.sendFax(faxNumber, pdfFile, rest);
        ele.dispatchEvent(new CustomEvent("fax-started", {
            bubbles: true,
            detail: {faxSid, pdfFile, faxNumber, pharmaName}
        }));
    };
    map.cancel.onclick = event => ele.dispatchEvent(new Event("cancel", {bubbles: true}));
    return ele;
}

function doView(file, rest){
    let url = rest.url("/show-pdf", {file: file});
    window.open(url, "_blank");
}

function getFilePart(path){
    let i = path.lastIndexOf("\\");
    if( i >= 0 ){
        return path.substring(i+1);
    }
    i = path.lastIndexOf("/");
    if( i >= 0 ){
        return path.substring(i+1);
    }
    return path;
}


