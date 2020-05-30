import * as kanjidate from "../js/kanjidate.js";
import { PatientSearch } from "./patient-search.js";
import { PatientDisplay } from "./patient-display.js";

export class ChoosePatientDialog {
    constructor(map, rest){
        let dialog = map.dialog;
        this.dialog = dialog.ele;
        this.search = new PatientSearch(dialog.search, rest);
        this.display = new PatientDisplay(dialog.display);
        this.enter = dialog.enter.ele;
        this.cancel = dialog.cancel.ele;
        this.thePatient = null;

        this.dialog.on("shown.bs.modal", event => this.search.focus());
        this.search.onSelect(patient => {
            this.display.setPatient(patient);
        });
        this.cancel.on("click", event => {
            this.dialog.modal("hide");
        });
    }

    async open(){
        return new Promise(resolve => {
            this.dialog.on("hidden.bs.modal", event => {
                resolve(this.thePatient);
            });
            this.dialog.modal("show");
        });
    }
}

