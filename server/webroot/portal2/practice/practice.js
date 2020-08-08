import {parseElement} from "../js/parse-element.js";
import {createDropdown} from "../comp/dropdown.js";
import {openSelectWqueueDialog} from "./select-wqueue-dialog.js";

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
                let patient = await openSelectWqueueDialog(ctx.rest);
                console.log("patient", patient);
            }
        },
        {
            label: "患者検索",
            action: () => {}
        },
        {
            label: "最近の診察",
            action: () => {}
        },
        {
            label: "本日の診察",
            action: () => {}
        },
        {
            label: "以前の診察",
            action: () => {}
        }
    ]);
}