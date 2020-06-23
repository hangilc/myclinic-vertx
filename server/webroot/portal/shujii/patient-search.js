import {Component} from "./component.js";
import * as kanjidate from "../js/kanjidate.js";

export class PatientSearch extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.formElement = map.form;
        this.searchTextElement = map.searchText;
        this.selectElement = map.select;
    }

    init(){
        this.formElement.on("submit", event => {
            let promise = this.doSearch();
            return false;
        });
        this.selectElement.on("change", event => this.doSelected());
        return this;
    }

    set(){
        return this;
    }

    focus(){
        this.searchTextElement.focus();
    }

    onSelected(cb){
        this.on("selected", (event, patient) => cb(patient));
    }

    doSelected(){
        let patient = this.selectElement.find("option:selected").data("patient");
        if( patient ){
            this.trigger("selected", patient);
        }
    }

    getSelectedPatient(){
        return this.selectElement.find("option:selected").data("patient");
    }

    setSearchResult(patients){
        this.selectElement.html("");
        for(let patient of patients){
            let opt = $("<option>").text(this.createPatientLabel(patient));
            opt.data("patient", patient);
            this.selectElement.append(opt);
        }
    }

    async doSearch(){
        let text = this.searchTextElement.val();
        if( !text ){
            return;
        }
        let result = await this.rest.searchPatient(text);
        sortPatients(result);
        this.setSearchResult(result);
    }

    createPatientLabel(patient){
        let patientIdRep = ("" + patient.patientId).padStart(4, "0");
        let birthday = kanjidate.sqldateToKanji(patient.birthday);
        return `(${patientIdRep}) ${patient.lastName}${patient.firstName} (${birthday}生)`;
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

