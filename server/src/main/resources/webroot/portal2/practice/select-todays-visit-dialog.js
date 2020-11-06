import {modalOpen} from "../comp/modal-dialog.js";
import {parseElement} from "../js/parse-element.js";

let html = `
<h3>本日の診察</h3>
<div>
    <select size="10" class="x-select"></select>
</div>
<div class="command-box">
    <button class="x-enter">選択</button>
    <button class="x-cancel">キャンセル</button>
</div>
`;

export async function openTodaysVisitDialog(rest){
    let ele = document.createElement("div");
    ele.classList.add("select-todays-visit-dialog");
    ele.innerHTML = html;
    let map = parseElement(ele);
    let list = await rest.listTodaysVisits(0);
    list.forEach(data => {
        let opt = createOption(data);
        map.select.append(opt);
    });
    return await modalOpen(ele, close => {
        map.enter.onclick = event => {
            let opt = map.select.options[map.select.selectedIndex];
            if( opt ){
                close(opt.data);
            }
        };
        map.cancel.onclick = event => close(null);
    });
}

function createOption(data){
    let patient = data.patient;
    let rep = `${patient.lastName}${patient.firstName}`;
    let opt = document.createElement("option");
    opt.innerText = rep;
    opt.data = data;
    return opt;
}

