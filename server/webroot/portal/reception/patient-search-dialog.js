import {Dialog} from "./dialog.js";
import {PatientSearch} from "./patient-search.js";
import {PatientDisp} from "./patient-disp.js";

export class PatientSearchDialog extends Dialog {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.search = new PatientSearch(map.search_, map.search, rest);
        this.disp = new PatientDisp(map.disp_, map.disp, rest);
        this.recentElement = map.search.recent;
        this.editElement = map.edit;
        this.registerElement = map.register;
    }

    init(){
        super.init();
        this.search.init();
        this.search.onSelected(patient => this.doSelected(patient));
        this.disp.init();
        this.setupDispConverters(this.disp);
        this.onOpened(() => this.focus());
        this.recentElement.on("click", event => this.doRecent());
        this.editElement.on("click", event => this.doEdit());
        this.registerElement.on("click", event => this.doRegister());
        return this;
    }

    set(){
        super.set();
        this.search.set();
        this.disp.set();
        return this;
    }

    focus(){
        this.search.focus();
    }

    doSelected(patient){
        this.patient = patient;
        this.disp.set(patient);
    }

    async doRecent(){
        let result = await this.rest.listRecentlyRegisteredPatients(20);
        this.search.setSearchResult(result);
    }

    setupDispConverters(disp){
        disp.setBirthdayConv(birthday => this.disp.birthdayAsKanji(birthday, {
            suffix: "生"
        }) + " " + this.disp.calcAge(birthday) + "才");
        disp.setSexConv(sex => this.disp.sexAsKanji(sex));
    }

    doEdit(){
        this.close({
            action: "edit",
            patient: this.patient
        });
    }

    doRegister(){
        alert("not implemented");
    }

}