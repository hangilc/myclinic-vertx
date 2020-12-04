import {Dialog} from "../js/dialog.js";
import {parseElement} from "../js/parse-node.js";
import * as kanjidate from "../js/kanjidate.js";

let bodyTmpl = `
    <form class="form-inline x-form" onsubmit="return false;">
        <input type="text" class="form-control x-search-text"/>
        <button type="submit" class="btn btn-secondary ml-2">検索</button>
    </form>
    <select size="10" class="x-select form-control mt-2"></select>
`;

let commandsTmpl = `
    <button type="button" class="btn btn-secondary mr-2 x-enter">選択</button>
    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class SelectPatientDialog extends Dialog {
    constructor(rest){
        super();
        this.rest = rest;
        this.setTitle("患者選択");
        this.getBody().innerHTML = bodyTmpl;
        this.bmap = parseElement(this.getBody());
        this.bmap.form.addEventListener("submit", async event => await this.doSearch());
        this.getFooter().innerHTML = commandsTmpl;
        this.cmap = parseElement(this.getFooter());
        this.cmap.enter.addEventListener("click", event => this.doEnter());
        this.cmap.cancel.addEventListener("click", event => this.close(null));
        this.ele.addEventListener("opened", event => this.bmap.searchText.focus());
    }

    async doSearch(){
        let text = this.bmap.searchText.value;
        if( !text ){
            return;
        }
        let result = await this.rest.searchPatient(text);
        sortPatients(result);
        this.setSearchResult(result);
    }

    setSearchResult(patients){
        this.bmap.select.innerHTML = "";
        for(let patient of patients){
            let opt = document.createElement("option");
            opt.innerText = createPatientLabel(patient);
            opt.data = patient;
            this.bmap.select.append(opt);
        }
    }

    doEnter(){
        let opt = this.bmap.select.querySelector("option:checked");
        if( opt ){
            this.close(opt.data);
        }
    }

}

function createPatientLabel(patient){
    let patientIdRep = ("" + patient.patientId).padStart(4, "0");
    let birthday = kanjidate.sqldateToKanji(patient.birthday);
    return `(${patientIdRep}) ${patient.lastName}${patient.firstName} (${birthday}生)`;
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

