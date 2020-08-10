import {parseElement} from "../js/parse-element.js";

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
        this.currentVisitId = 0;
    }

    adaptUI(){
        let map = this.map;
        [map.cashier, map.registerExam].forEach(
            e => {
                if( this.currentVisitId === 0 ){
                    e.classList.add("hidden");
                } else {
                    e.classList.remove("hidden");
                }
            }
        );
    }
}

export function createPatientManip(registerPatientCallback, registerVisitIdCallback){
    let ele = document.createElement("div");
    ele.classList.add("patient-manip");
    ele.innerHTML = html;
    let map = parseElement(ele);
    let ctx = new Context(map);
    ctx.adaptUI();
    registerPatientCallback(patient => {
        ctx.patientId = patient.patientId;
    });
    registerVisitIdCallback(visitId => {
        ctx.currentVisitId = visitId;
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