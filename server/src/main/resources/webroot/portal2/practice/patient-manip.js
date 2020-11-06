import {parseElement} from "../js/parse-element.js";
import * as F from "./functions.js";

let html = `
<button class="x-cashier">会計</button>
<button class="x-end-patient">患者終了</button> 
<a href="javascript:void(0)" class="x-register-exam">診察登録</a> <span class="sep">|</span>
<a href="javascript:void(0)" class="x-search-text">文章検索</a> <span class="sep">|</span>
<a href="javascript:void(0)" class="x-refer">紹介状作成</a> <span class="sep">|</span>
<a href="javascript:void(0)" class="x-save-image">画像保存</a>
`;

class Context {
    constructor(map) {
        this.map = map;
        this.patientId = 0;
        this.visitId = 0;
    }

    adaptUI(){
        let map = this.map;
        let hasVisit = this.visitId !== 0;
        F.showHide(map.cashier, hasVisit);
        F.showHide(map.registerExam, !hasVisit);
    }
}

export function createPatientManip(onPatientChanged, onVisitIdChanged){
    let ele = document.createElement("div");
    ele.classList.add("patient-manip", "hidden");
    ele.innerHTML = html;
    let map = parseElement(ele);
    let ctx = new Context(map);
    ctx.adaptUI();
    onPatientChanged((patient) => {
        if( patient ){
            ctx.patientId = patient.patientId;
            ctx.adaptUI();
            F.show(ele);
        } else {
            F.hide(ele);
        }
    });
    onVisitIdChanged(currentVisitId => {
        ctx.visitId = currentVisitId;
        ctx.adaptUI();
    });
    map.cashier.onclick = event => dispatchEvent(ele, "do-cashier");
    map.endPatient.onclick = event => dispatchEvent(ele, "do-end-patient");
    return ele;
}

function dispatchEvent(ele, name){
    let evt = new Event(name, {bubbles: true});
    ele.dispatchEvent(evt);

}