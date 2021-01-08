import {Widget} from "../../js/widget.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {click, on} from "../../js/dom-helper.js";

export class WqueueWidget extends Widget {
    constructor(patients) {
        super();
        this.patients = patients;
        this.setTitle("受付患者");
        this.updateItems();
    }

    updateItems(){
        const e = this.getBody();
        e.innerHTML = "";
        this.patients.forEach(patient => {
            const item = new Item(patient);
            e.append(item.ele);
        })
    }
}

const itemTmpl = `
    <div>
        <a href="javascript:void(0)" class="x-label"></a>
    </div>
`;

class Item {
    constructor(patient){
        this.ele = createElementFrom(itemTmpl);
        const map = parseElement(this.ele);
        const patientIdRep = `${patient.patientId}`.padStart(4, "0");
        map.label.innerText = `(${patientIdRep}) ${patient.lastName}${patient.firstName}`;
        click(map.label, event => this.ele.dispatchEvent(
            new CustomEvent("patient-clicked", {
            bubbles: true,
            detail: patient
        })));
    }
}