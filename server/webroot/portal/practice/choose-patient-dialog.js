import * as kanjidate from "../js/kanjidate.js";
import { PatientSearch } from "./patient-search.js";
import { PatientDisplay } from "./patient-display.js";

export class ChoosePatientDialog {
    constructor(map, rest){
        this.dialog = map.dialog;
        this.search = new PatientSearch(map.search, rest);
        this.display = new PatientDisplay(map.display);
        this.registerEnter = map.registerEnter;
        this.enter = map.enter;
        this.cancel = map.cancel;
        this.thePatient = null;
        this.theResult = {
            mode: "cancel",
            patient: null
        };

        this.dialog.on("shown.bs.modal", event => this.search.focus());
        this.search.onSelect(patient => {
            this.display.setPatient(patient);
            this.thePatient = patient;
        });
        this.registerEnter.on("click", event => {
            if( !this.thePatient ){
                return;
            }
            this.theResult = {
                mode: "register-enter",
                patient: this.thePatient
            };
            this.dialog.modal("hide");
        });
        this.enter.on("click", event => {
            if( !this.thePatient ){
                return;
            }
            this.theResult = {
                mode: "enter",
                patient: this.thePatient
            };
            this.dialog.modal("hide");
        });
        this.cancel.on("click", event => {
            this.dialog.modal("hide");
        });
    }

    async open(){
        return new Promise(resolve => {
            this.dialog.on("hidden.bs.modal", event => {
                resolve(this.theResult);
            });
            this.dialog.modal("show");
        });
    }
}

