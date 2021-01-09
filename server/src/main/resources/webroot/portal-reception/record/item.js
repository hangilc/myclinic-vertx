import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {click} from "../../js/dom-helper.js";
import {patientIdRep} from "../../js/patient-util.js";

const itemTmpl = `
    <div>
        <a href="javascript:void(0)" class="x-label"></a>
        <span class="x-detail" style="font-size:80%"></span>
    </div>
`;

export class Item {
    constructor(patient){
        this.ele = createElementFrom(itemTmpl);
        const map = this.map = parseElement(this.ele);
        map.label.innerText = `(${patientIdRep(patient.patientId)}) ${patient.lastName}${patient.firstName}`;
        click(map.label, event => this.ele.dispatchEvent(
            new CustomEvent("patient-clicked", {
                bubbles: true,
                detail: patient
            })));
    }

    setDetail(detail){
        this.map.detail.innerText = detail;
    }
}