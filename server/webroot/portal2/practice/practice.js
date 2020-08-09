import {parseElement} from "../js/parse-element.js";
import {createDropdown} from "../comp/dropdown.js";
import {openSelectWqueueDialog} from "./select-wqueue-dialog.js";
import {openSearchPatientDialog} from "./search-patient-dialog.js";
import {openSelectRecentVisitDialog} from "./select-recent-visit-dialog.js";
import {openTodaysVisitDialog} from "./select-todays-visit-dialog.js";
import {openPrevVisitDialog} from "./select-prev-visit-dialog.js";
import {createPatientInfo} from "./patient-info.js";
import {createPatientManip} from "./patient-manip.js";

let tmpl = `
<h2>診察</h2>
<div>
    <a href="javascript:void(0)" class="x-choose-patient">患者選択▼</a> |
    <a href="javascript:void(0)" class="x-registered-drug">登録薬剤</a> |
    <a href="javascript:void(0)" class="x-search-whole-text">全文検索</a>
</div>
<div class="x-workarea">
    <div class="x-patient-info"></div>
    <div class="x-patient-manip"></div>
</div>
`;

class Context {
    constructor(rest){
        this.rest = rest;
        this.patient = null;
        this.currentVisitId = 0;
        this.tempVisitId = 0;
        this.patientChangedCallbacks = [];
        this.visitIdChangedCallbacks = [];
    }

    setPatient(patient){
        this.patient = patient;
        this.patientChangedCallbacks.forEach(cb => cb(patient));
    }
}

export function createPractice(rest){
    let ctx = new Context(rest);
    let ele = document.createElement("div");
    ele.classList.add("practice");
    ele.innerHTML = tmpl;
    let map = parseElement(ele);
    let regPatientChanged = callback => ctx.patientChangedCallbacks.push(callback);
    let regVisitIdChanged = callback => ctx.visitIdChangedCallbacks.push(callback);
    regPatientChanged(patient => {
        [map.patientInfo, map.patientManip].forEach(e => {
            e.style.display = patient ? "block" : "none";
        });
    });
    ctx.setPatient(null);
    setupChoosePatient(map.choosePatient, ctx);
    map.patientInfo.append(createPatientInfo(regPatientChanged));
    map.patientManip.append(createPatientManip(regPatientChanged, regVisitIdChanged));
    ele.addEventListener("do-cashier", event => console.log("do-cashier"));
    return ele;
}

function setupChoosePatient(ele, ctx){
    createDropdown(ele, [
        {
            label: "受付患者選択",
            action: async () => {
                let wqueueFull = await openSelectWqueueDialog(ctx.rest);
                if( wqueueFull ){
                    ctx.setPatient(wqueueFull.patient);
                }
            }
        },
        {
            label: "患者検索",
            action: async () => {
                let result = await openSearchPatientDialog(ctx.rest);
                if( result ){
                    ctx.setPatient(result.patient);
                }
            }
        },
        {
            label: "最近の診察",
            action: async () => {
                let visitPatient = await openSelectRecentVisitDialog(ctx.rest);
                if( visitPatient ){
                    ctx.setPatient(visitPatient.patient);
                }
            }
        },
        {
            label: "本日の診察",
            action: async () => {
                let visitPatient = await openTodaysVisitDialog(ctx.rest);
                if( visitPatient ){
                    ctx.setPatient(visitPatient.patient);
                }
            }
        },
        {
            label: "以前の診察",
            action: async () => {
                let visitPatient = await openPrevVisitDialog(ctx.rest);
                if( visitPatient ){
                    ctx.setPatient(visitPatient.patient);
                }
            }
        }
    ]);
}