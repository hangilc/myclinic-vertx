import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {PatientDisp} from "../components/patient-disp.js";


let tmpl = `
<div class="mb-3 border border-success rounded p-2">
    <h6>患者検索結果</h6>
    <div class="row mb-2">
        <div class="col-6">
            <select class="form-control mb-2 x-search-result" size="8"></select>
        </div>
        <div class="col-6">
            <div class="mb-2 x-disp"></div>
            <div class="text-right"> 
                <button class="btn btn-primary btn-sm x-start-visit">診察受付</button> 
                <button class="btn btn-secondary btn-sm x-manage-patient">患者管理</button> 
                <button class="btn btn-secondary btn-sm x-close">閉じる</button>
           </div>
        </div>
    </div>
</div>
`;

export class PatientSearchResultBox {
    constructor(rest){
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        let disp = new PatientDisp(null);
        this.disp = disp;
        this.map.disp.appendChild(disp.ele);
        let selectedPatientId = 0;
        this.map.searchResult.addEventListener("change", async event => {
            let opt = this.map.searchResult.querySelector("option:checked");
            if( opt ){
                let patientId = parseInt(opt.value);
                let patient = await this.rest.getPatient(patientId);
                disp.set(patient);
                selectedPatientId = patientId;
            } else {
                disp.clear();
                selectedPatientId = 0;
            }
        });
        this.map.startVisit.addEventListener("click", async event => {
            if( selectedPatientId > 0 ){
                await this.doStartVisit(selectedPatientId);
            }
        });
        this.map.close.addEventListener("click", event => this.ele.dispatchEvent(new Event("close")));
    }

    async doStartVisit(patientId){
        if( patientId > 0 ){
            await this.rest.startVisit(patientId);
            this.ele.dispatchEvent(new Event("close"));
        }
    }

    setResult(patients){
        this.map.searchResult.innerHTML = "";
        for(let patient of patients){
            let opt = createOption(patient);
            this.map.searchResult.appendChild(opt);
        }
    }
}

function createOption(patient){
    let opt = document.createElement("option");
    let patientIdLabel = ("" + patient.patientId).padStart(4, "0");
    let nameLabel = `${patient.lastName}${patient.firstName}`;
    opt.innerText = `[${patientIdLabel}] ${nameLabel}`;
    opt.value = patient.patientId;
    return opt;
}