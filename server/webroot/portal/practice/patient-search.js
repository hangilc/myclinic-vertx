import * as kanjidate from "../js/kanjidate.js";

export class PatientSearch {
    constructor(map, rest) {
        this.rest = rest;
        this.form = map.form;
        this.textInput = map.input;
        this.resultSelect = map.select;
        this.onSelectCallbacks = [];

        this.form.on("submit", async event => {
            event.preventDefault();
            let result = await this.rest.searchPatient(this.textInput.val());
            sortPatients(result);
            let select = this.resultSelect;
            select.html("");
            for(let p of result){
                let opt = $("<option>").text(makePatientLabel(p)).data("patient", p);
                select.append(opt);
            }
        });

        this.resultSelect.on("change", event => {
            let opt = this.resultSelect.find("option:selected");
            let patient = null;
            if( opt ){
                patient = opt.data("patient");
            }
            this.onSelectCallbacks.forEach(f => f(patient));
        });
    }

    focus(){
        this.textInput.focus();
    }

    onSelect(f){
        this.onSelectCallbacks.push(f);
    }
}

function cmp(...props){
    return (a, b) => {
        for(let p of props){
            let pa = a[p];
            let pb = b[p];
            if( pa < pb ){
                return -1;
            } else if( pa > pb ){
                return 1;
            }
        }
        return 0;
    };
}

let cmpPatient = cmp("lastNameYomi", "firstNameYomi", "patientId");

function sortPatients(patientList){
    patientList.sort(cmpPatient);
}

function makePatientLabel(patient){
    let patientIdRep = ("" + patient.patientId).padStart(4, "0");
    let birthday = kanjidate.sqldateToKanji(patient.birthday);
    return `(${patientIdRep}) ${patient.lastName}${patient.firstName} (${birthday}生)`;
}
