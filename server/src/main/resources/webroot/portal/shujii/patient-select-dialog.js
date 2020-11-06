import {Dialog} from "./dialog.js";
import {PatientSearch, sortPatients} from "./patient-search.js";

export class PatientSelectDialog extends Dialog {
    constructor(ele, map, rest){
        super(map.dialog, map, rest);
        this.search = new PatientSearch(map.search_, map.search, rest);
        this.prevElement = map.search.prev;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
    }

    init(){
        super.init();
        this.search.init();
        this.prevElement.on("click", event => this.doPrev());
        this.cancelElement.on("click", event => this.close(null));
        this.enterElement.on("click", event => this.doEnter());
        this.ele.on("shown.bs.modal", event => this.search.focus());
        return this;
    }

    async doPrev(){
        let patientIds = await this.rest.listShujiiPatient();
        let patients = await this.rest.batchGetPatient(patientIds);
        sortPatients(patients);
        this.search.setSearchResult(patients);
    }

    doEnter(){
        let patient = this.search.getSelectedPatient();
        if( patient ){
            this.close(patient);
        }
    }

    set(){
        super.set();
        this.search.set();
        return this;
    }
}
