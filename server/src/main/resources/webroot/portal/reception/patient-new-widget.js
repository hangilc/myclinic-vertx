import {Widget} from "./widget2.js";
import {PatientForm} from "./patient-form.js";
import {parseElement} from "../js/parse-node.js";

let commandsHtml = `
    <button type="button" class="x-enter btn btn-secondary">入力</button>
    <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
`;

let symEntered = Symbol("entered");

export class PatientNewWidget extends Widget {
    constructor(rest) {
        super();
        this.setTitle("新規患者登録");
        this.rest = rest;
        let form = new PatientForm();
        this.getContentElement().append(form.ele);
        this.form = form;
        form.hidePatientId();
        let commands = this.getCommandsElement();
        commands.innerHTML = commandsHtml;
        let cmap = parseElement(commands);
        cmap.enter.addEventListener("click", async event => await this.doEnter());
        cmap.close.addEventListener("click", event => this.close());
        this[symEntered] = null;
    }

    async doEnter(){
        let patient = this.form.get();
        if( !patient ){
            let err = this.form.getError();
            if( err ){
                alert(err);
            }
        } else {
            delete patient.patientId;
            patient.patientId = await this.rest.enterPatient(patient);
            this.close();
            let cb = this[symEntered];
            if( cb ){
                cb(patient);
            }
        }
    }

    onEntered(cb){
        this[symEntered] = cb;
    }
}