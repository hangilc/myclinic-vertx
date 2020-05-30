import * as kanjidate from "../js/kanjidate.js";

export class ChoosePatientDialog {
    constructor(props){
        this.dialog = props.dialog;

        //this.dialog.on("shown.bs.modal", event => this.patientSearch.focus());
    }

    async open(){
        return new Promise(resolve => {
            this.dialog.on("hidden.bs.modal", event => {
                resolve(null);
            });
            this.dialog.modal("show");
        });
    }
}

