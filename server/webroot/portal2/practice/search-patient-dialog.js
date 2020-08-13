import {parseElement} from "../js/parse-element.js";
import {modalOpen} from "../comp/modal-dialog.js";
import {compareBy} from "../../portal/js/general-util.js";
import * as kanjidate from "../../portal/js/kanjidate.js";

let html = `
<h3>患者検索</h3>
<div>
    <form class="x-form">
        <input type="text" class="x-input"> <button type="submit">検索</button>
    </form>
</div>
<div>
    <select size="10" class="x-select"></select>
</div>
<div class="command-box">
    <button class="x-register">受付</button>
    <button class="x-enter">選択</button>
    <button class="x-cancel">キャンセル</button>
</div>
`;

let comparePatient = compareBy("lastNameYomi", "firstNameYomi", "patientId");

export async function openSearchPatientDialog(rest){
    let ele = document.createElement("div");
    ele.classList.add("search-patient-dialog");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.form.onsubmit = async event => {
        event.preventDefault();
        let text = map.input.value;
        let result = await rest.searchPatient(text);
        result.sort(comparePatient);
        map.select.innerHTML = "";
        for(let p of result){
            let opt = createOption(p);
            map.select.append(opt);
        }
    };
    return await modalOpen(ele, close => {
        map.register.onclick = event => {
            let opt = map.select.options[map.select.selectedIndex];
            if( opt ){
                close({register: true, patient: opt.data});
            }
        }
        map.enter.onclick = event => {
            let opt = map.select.options[map.select.selectedIndex];
            if( opt ){
                close({patient: opt.data});
            }
        }
        map.cancel.onclick = event => close(null);
    });
}

function createOption(patient){
    let opt = document.createElement("option");
    opt.innerText = makePatientLabel(patient);
    opt.data = patient;
    return opt;
}

function makePatientLabel(patient){
    let patientIdRep = ("" + patient.patientId).padStart(4, "0");
    let birthday = kanjidate.sqldateToKanji(patient.birthday);
    return `(${patientIdRep}) ${patient.lastName}${patient.firstName} (${birthday}生)`;
}

