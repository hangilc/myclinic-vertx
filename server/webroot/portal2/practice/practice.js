import {parseElement} from "../js/parse-element.js";
import {createDropdown} from "../comp/dropdown.js";
import {openSelectWqueueDialog} from "./select-wqueue-dialog.js";
import {openSearchPatientDialog} from "./search-patient-dialog.js";
import {openSelectRecentVisitDialog} from "./select-recent-visit-dialog.js";
import {openTodaysVisitDialog} from "./select-todays-visit-dialog.js";
import {openPrevVisitDialog} from "./select-prev-visit-dialog.js";

let tmpl = `
<h2>診察</h2>
<div>
    <a href="javascript:void(0)" class="x-choose-patient">患者選択▼</a> |
    <a href="javascript:void(0)" class="x-registered-drug">登録薬剤</a> |
    <a href="javascript:void(0)" class="x-search-whole-text">全文検索</a>
</div>
`;

class Context {
    constructor(rest){
        this.rest = rest;
        this.patient = null;
        this.currentVisitId = 0;
        this.tempVisitId = 0;
    }
}

export function createPractice(rest){
    let ctx = new Context(rest);
    let ele = document.createElement("div");
    ele.innerHTML = tmpl;
    let map = parseElement(ele);
    setupChoosePatient(map.choosePatient, ctx);
    return ele;
}

function setupChoosePatient(ele, ctx){
    createDropdown(ele, [
        {
            label: "受付患者選択",
            action: async () => {
                let wqueueFull = await openSelectWqueueDialog(ctx.rest);
            }
        },
        {
            label: "患者検索",
            action: async () => {
                let result = await openSearchPatientDialog(ctx.rest);
                console.log("result", result);
            }
        },
        {
            label: "最近の診察",
            action: async () => {
                let visitPatient = await openSelectRecentVisitDialog(ctx.rest);
                console.log("visit-patient", visitPatient);
            }
        },
        {
            label: "本日の診察",
            action: async () => {
                let visitPatient = await openTodaysVisitDialog(ctx.rest);
                console.log("visit-patient", visitPatient);
            }
        },
        {
            label: "以前の診察",
            action: async () => {
                let visitPatient = await openPrevVisitDialog(ctx.rest);
                console.log("visit-patient", visitPatient);
            }
        }
    ]);
}