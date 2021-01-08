import {Widget} from "../../../js/widget.js";
import {parseElement} from "../../../js/parse-node.js";
import {click} from "../../../js/dom-helper.js";
import {createElementFrom} from "../../../js/create-element-from.js";

let footerTmpl = `
    <button class="btn btn-link x-close">閉じる</button>
`;

export class PatientListWidget extends Widget {
    constructor(prop, title, patients) {
        super();
        this.prop = prop;
        this.setTitle(title);
        this.setPatients(patients);
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.close, event => this.close());
        this.getBody().addEventListener("select-patient", event => {
            event.stopPropagation();
            let body = this.getBody();
            let patientId = event.detail;
            this.prop.endSession();
            this.prop.startSession(patientId);
            body.querySelectorAll(".patient-list-item").forEach(
                e => e.dispatchEvent(new Event("clear-bold")));
            body.querySelector(`.patient-list-item[data-patient-id='${patientId}']`)
                .dispatchEvent(new Event("bold"));
        });
    }

    setPatients(patients) {
        let wrapper = this.getBody();
        patients.forEach(patient => {
            let item = new Item(patient);
            wrapper.append(item.ele);
        })
    }
}

let itemTmpl = `
    <div class="patient-list-item" data-patient-id="0">
        <a href="javascript:void(0)" class="x-link"><span class="x-label"></span></a>
    </div>
`;

class Item {
    constructor(patient) {
        this.patient = patient;
        this.ele = createElementFrom(itemTmpl);
        this.ele.dataset.patientId = patient.patientId;
        let map = this.map = parseElement(this.ele);
        let patientIdRep = ("" + patient.patientId).padStart(4, "0");
        map.label.innerText = `(${patientIdRep}) ${patient.lastName} ${patient.firstName}`;
        this.ele.addEventListener("bold", event => this.bold());
        this.ele.addEventListener("clear-bold", event => this.clearBold());
        click(map.link, event => this.ele.dispatchEvent(new CustomEvent("select-patient",
            {bubbles: true, detail: patient.patientId})));
    }

    getPatient() {
        return this.patient;
    }

    clearBold() {
        this.map.label.classList.remove("font-weight-bold");
    }

    bold() {
        this.map.label.classList.add("font-weight-bold");
    }
}