import {parseElement} from "../js/parse-element.js";
import * as F from "./functions.js";

let html = `
<div class="x-title title"></div>
<div>
    <span class="x-pdf-file"></span>
    <a href="javascript:void(0)" class="x-view">表示</a>
</div>
<div class="x-pharma-name"></div>
<div class="x-fax-number"></div>
<div class="x-message"></div>
<div class="command-box">
    <button class="x-re-send">再送信</button>
    <a href="javascript:void(0)" class="x-close">閉じる</a>
</div>
`;

export function createFaxProgress(patientName, faxSid, pdfFile, faxNumber, pharmaName, rest){
    let ele = document.createElement("div");
    ele.classList.add("fax-progress");
    ele.innerHTML = html;
    let done = null;
    let map = parseElement(ele);
    map.title.innerText = patientName;
    map.pdfFile.innerText = getFilePart(pdfFile);
    map.view.onclick = event => doView(pdfFile, rest);
    map.pharmaName.innerText = pharmaName;
    map.faxNumber.innerText = faxNumber;
    map.close.onclick = event => {
        if( !done ){
            if( !confirm("まだ送信を終了していませんが、閉じますか？") ){
                return;
            }
        }
        ele.remove();
    }
    map.reSend.onclick = async event => {
        if( !done ){
            alert("まだ送信が終了していないので、再送信を開始できません。");
            return;
        }
        map.message.innerHTML = "";
        faxSid = await F.sendFax(faxNumber, pdfFile, rest);
        F.reStartPollFax(faxSid, msg => addMessage(map.message, msg),
            status => done = status, rest);
    };
    F.startPollFax(faxSid, msg => addMessage(map.message, msg),
            status => done = status, rest);
    return ele;
}

function addMessage(wrapper, msg){
    let e = document.createElement("div");
    e.innerText = msg;
    wrapper.append(e);
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
