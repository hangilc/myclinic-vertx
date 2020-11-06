import {modalOpen} from "../comp/modal-dialog.js";
import {parseElement} from "../js/parse-element.js";

let html = `
<h3>以前の診察</h3>
<div>
    診察日 <input type="date" class="x-date">
</div>
<div>
    <select size="10" class="x-select"></select>
</div>
<div class="command-box">
    <button class="x-enter">選択</button>
    <button class="x-cancel">キャンセル</button>
</div>
`;

export async function openPrevVisitDialog(rest){
    let ele = document.createElement("div");
    ele.classList.add("select-prev-visit-dialog");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.date.onchange = async event => {
        let date = map.date.value;
        let list = await rest.listVisitPatientAt(date);
        list.forEach(data => {
            let opt = createOption(data);
            map.select.append(opt);
        });
    }
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

