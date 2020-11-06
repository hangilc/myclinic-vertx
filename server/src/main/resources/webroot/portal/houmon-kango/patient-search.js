import * as kanjidate from "../js/kanjidate.js";

export class PatientSearch {
    constructor(rest, form, searchText, select) {
        this.searchTextElement = searchText;
        this.selectElement = select;
        form.on("submit", async event => {
            event.preventDefault();
            let text = searchText.val();
            let result = await rest.searchPatient(text);
            result.sort(cmpPatient);
            select.html("");
            for(let p of result){
                let opt = $("<option>").text(makePatientLabel(p)).data("patient", p);
                select.append(opt);
            }
        });
    }

    focus(){
        this.searchTextElement.focus();
    }

    getSelectedData(){
        return this.selectElement.find("option:selected").data("patient");
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

function makePatientLabel(patient){
    let patientIdRep = ("" + patient.patientId).padStart(4, "0");
    let birthday = kanjidate.sqldateToKanji(patient.birthday);
    return `(${patientIdRep}) ${patient.lastName}${patient.firstName} (${birthday}生)`;

}
