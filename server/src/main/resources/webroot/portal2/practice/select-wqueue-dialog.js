import {modalOpen} from "../comp/modal-dialog.js";
import {parseElement} from "../js/parse-element.js";
import {wqueueStateCodeToRep} from "../../portal/js/consts.js";

let tmpl = `
<h3>受付患者選択</h3>
<div>
    <select size="10" class="x-select"></select>
</div>
<div class="command-box">
    <button class="x-enter">入力</button>
    <button class="x-cancel">キャンセル</button>
</div>
`;

export async function openSelectWqueueDialog(rest){
    let e = document.createElement("div");
    e.classList.add("select-wqueue-dialog");
    e.innerHTML = tmpl;
    let map = parseElement(e);
    let list = await rest.listWqueueFullForExam();
    let opts = list.map(wq => createOption(wq));
    opts.forEach(opt => map.select.append(opt));
    return await modalOpen(e, close => {
        map.enter.onclick = event => {
            let opt = map.select.options[map.select.selectedIndex];
            if( opt ){
                close(opt.data);
            }
        };
        map.cancel.onclick = event => {
            close(null);
        };
    });
}

function createOption(wqueueFull){
    let stateRep = wqueueStateCodeToRep(wqueueFull.wqueue.waitState);
    let patient = wqueueFull.patient;
    let rep = `[${stateRep}] ${patient.lastName}${patient.firstName}`;
    let opt = document.createElement("option");
    opt.innerText = rep;
    opt.data = wqueueFull;
    return opt;
}

