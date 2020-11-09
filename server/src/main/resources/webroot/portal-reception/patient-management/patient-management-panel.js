import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {NewPatientWidget} from "./new-patient-widget.js";
import {PatientWidget} from "./patient-widget.js";
import {PatientSearchResultBox} from "./patient-search-result-box.js";

let tmpl = `
<div>
    <div class="mb-3 form-inline">
        <div class="h3">患者管理</div>
        <button class="btn btn-sm btn-primary ml-auto mr-2 x-new-patient">新規患者</button>
        <input class="form-control form-control-sm mr-2 x-patient-search-text"/>
        <button class="btn btn-sm btn-primary x-patient-search-button">検索</button>
    </div>
    <div class="mb-3 x-patient-search-result"></div>
   <div class="x-workarea pt-2"></div>
</div>
`;

export class PatientManagementPanel {
    constructor(rest){
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.newPatient.addEventListener("click", event => this.doNewPatient());
        this.map.patientSearchButton.addEventListener("click", async event => await this.doPatientSearch());
        // (async function(self){
        //     let patient = await rest.getPatient(3);
        //     let w = new PatientWidget(patient, rest);
        //     await w.init();
        //     self.map.workarea.append(w.ele);
        // })(this);
    }

    async doPatientSearch(){
        let text = this.map.patientSearchText.value.trim();
        if( text === "" ){
            return;
        }
        let result = await this.rest.searchPatient(text);
        let box = new PatientSearchResultBox(this.rest);
        this.map.patientSearchResult.innerHTML = "";
        this.map.patientSearchResult.appendChild(box.ele);
        box.ele.addEventListener("close", event => {
            box.ele.remove();
            this.map.patientSearchText.value = "";
        });
        box.setResult(result);
    }

    doNewPatient(){
        let w = new NewPatientWidget(this.rest);
        w.ele.addEventListener("patient-entered", event => {
            let patient = event.detail;
            console.log(patient);
            w.close();
        });
        this.map.workarea.prepend(w.ele);
    }
}