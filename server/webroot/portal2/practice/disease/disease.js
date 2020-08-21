import {parseElement} from "../../js/parse-element.js";
import {createDiseaseCurrent} from "./disease-current.js";
import {createDiseaseAdd} from "./disease-add.js";
import {createDiseaseEnd} from "./disease-end.js";
import {createDiseaseSelect} from "./disease-select.js";
import {createDiseaseEdit} from "./disease-edit.js";
import * as F from "../functions.js";

let html = `
<div>
    <h3>病名</h3>
    <div class="x-workspace"></div>
    <div class="disease-menu">
        <a href="javascript:void(0)" class="x-current-link">現行</a> |
        <a href="javascript:void(0)" class="x-add-link">追加</a> |
        <a href="javascript:void(0)" class="x-end-link">転機</a> |
        <a href="javascript:void(0)" class="x-edit-link">編集</a>
    </div>
</div>
`;

export function initDiseaseArea(ele, onPatientChanged, rest){
    ele.innerHTML = html;
    F.hide(ele);
    let map = parseElement(ele);
    let diseaseFulls = [];
    let currentPatient = null;
    let examples = null;
    onPatientChanged(async patient => {
        currentPatient = patient;
        if( patient ){
            diseaseFulls = await rest.listCurrentDisease(patient.patientId);
            showCurrent();
            F.show(ele);
        } else {
            F.hide(ele);
        }
    });
    map.currentLink.onclick = event => showCurrent();
    map.addLink.onclick = event => showAdd();
    map.endLink.onclick = event => showEnd();
    map.editLink.onclick = event => showSelect();
    ele.addEventListener("disease-entered", event => {
        let entered = event.detail;
        diseaseFulls.push(entered);
    });
    ele.addEventListener("current-diseases-changed", event => {
        diseaseFulls = event.detail;
    });

    function showCurrent(){
        map.workspace.innerHTML = "";
        map.workspace.append(createDiseaseCurrent(diseaseFulls));
    }

    async function showAdd(){
        if( examples == null ){
            examples = await rest.listDiseaseExample();
        }
        let recent = await rest.getMostRecentVisitOfPatient(currentPatient.patientId);
        if( recent ){
            map.workspace.innerHTML = "";
            map.workspace.append(createDiseaseAdd(diseaseFulls,
                recent.visitedAt.substring(0, 10),
                currentPatient.patientId,
                examples,
                rest));
        }
    }

    function showEnd(){
        if( currentPatient ){
            map.workspace.innerHTML = "";
            map.workspace.append(createDiseaseEnd(diseaseFulls, currentPatient.patientId, rest));
        }
    }

    async function showSelect(){
        if( currentPatient ){
            map.workspace.innerHTML = "";
            let diseaseFulls = await rest.listDisease(currentPatient.patientId);
            let e = createDiseaseSelect(diseaseFulls);
            e.addEventListener("disease-selected", event => showEdit(event.detail));
            map.workspace.append(e);
        }
    }

    async function showEdit(diseaseFull){
        map.workspace.innerHTML = "";
        map.workspace.append(createDiseaseEdit(diseaseFull));
    }
}
