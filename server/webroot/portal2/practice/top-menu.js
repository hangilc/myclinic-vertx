import {parseElement} from "../js/parse-element.js";
import {createDropdown} from "../comp/dropdown.js";
import {openSelectWqueueDialog} from "./select-wqueue-dialog.js";
import {openSearchPatientDialog} from "./search-patient-dialog.js";
import {openSelectRecentVisitDialog} from "./select-recent-visit-dialog.js";
import {openTodaysVisitDialog} from "./select-todays-visit-dialog.js";
import {openPrevVisitDialog} from "./select-prev-visit-dialog.js";

let html = `
<a href="javascript:void(0)" class="x-choose-patient">患者選択▼</a> |
<a href="javascript:void(0)" class="x-registered-drug">登録薬剤</a> |
<a href="javascript:void(0)" class="x-search-whole-text">全文検索</a>
`;

export function populateTopMenu(ele, rest){
    ele.innerHTML = html;
    let map = parseElement(ele);
    setupChoosePatient(map.choosePatient, rest);
}

function setupChoosePatient(ele, rest){
    createDropdown(ele, [
        {
            label: "受付患者選択",
            action: async () => {
                let wqueueFull = await openSelectWqueueDialog(rest);
                if( wqueueFull ) {
                    dispatchEvent(ele, wqueueFull.patient, wqueueFull.visit.visitId);
                }
            }
        },
        {
            label: "患者検索",
            action: async () => {
                let result = await openSearchPatientDialog(rest);
                if( result ) {
                    dispatchEvent(ele, result.patinet, 0, result.register);
                }
            }
        },
        {
            label: "最近の診察",
            action: async () => {
                let visitPatient = await openSelectRecentVisitDialog(rest);
                if( visitPatient ) {
                    dispatchEvent(ele, visitPatient.patient, 0);
                }
            }
        },
        {
            label: "本日の診察",
            action: async () => {
                let visitPatient = await openTodaysVisitDialog(rest);
                if( visitPatient ) {
                    dispatchEvent(ele, visitPatient.patient, 0);
                }
            }
        },
        {
            label: "以前の診察",
            action: async () => {
                let visitPatient = await openPrevVisitDialog(rest);
                if( visitPatient ) {
                    dispatchEvent(ele, visitPatient.patient, 0);
                }
            }
        }
    ]);
}

function dispatchEvent(ele, patient, visitId=0, registerForExam=false){
    let detail = {
        patient,
        visitId,
        registerForExam
    }
    let evt = new CustomEvent("open-patient", {bubbles: true, detail});
    ele.dispatchEvent(evt);
}
