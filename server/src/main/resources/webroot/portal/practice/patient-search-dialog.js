import {Dialog} from "./dialog.js";
import {compareBy} from "../js/general-util.js";
import * as kanjidate from "../js/kanjidate.js";
import {PatientDisp} from "./patient-disp.js";
import {sexAsKanji} from "../js/sex-util.js";

export class PatientSearchDialog extends Dialog {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.search = map.search;
        this.disp = new PatientDisp(map.disp);
        this.registerEnterElement = map.registerEnter;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
        this.patientCmp = compareBy("lastNameYomi", "firstNameYomi", "patientId");
    }

    init(){
        super.init();
        this.search.form.on("submit", event => {
            let promise = this.doSearch();
            return false;
        });
        this.search.select.on("change", event => this.doSelectionChanged());
        this.registerEnterElement.on("click", event => this.doRegisterEnter());
        this.enterElement.on("click", event => this.doEnter());
        this.cancelElement.on("click", event => this.hide());
        this.ele.on("shown.bs.modal", event => this.search.input.focus());
    }

    set(){
        super.set();
    }

    getSelectedPatient(){
        return this.search.select.find("option:selected").data("patient");
    }

    doSelectionChanged(){
        let patient = this.getSelectedPatient();
        this.disp.set(Object.assign({}, patient, {
            birthday: formatBirthday(patient.birthday),
            sex: sexAsKanji(patient.sex)
        }));
    }

    async doRegisterEnter(){
        let patient = this.getSelectedPatient();
        if( patient ){
            let visitId = await this.rest.startVisit(patient.patientId);
            this.setDialogResult({
                mode: "register-enter",
                patient: patient,
                visitId: visitId
            });
            this.close();
        }
    }

    doEnter(){
        let patient = this.getSelectedPatient();
        if( patient ){
            this.setDialogResult({
                mode: "enter",
                patient: patient
            });
            this.close();
        }
    }

    async doSearch(){
        let text = this.search.input.val();
        let result = await this.rest.searchPatient(text);
        result.sort(this.patientCmp);
        let select = this.search.select;
        select.html("");
        for(let p of result){
            let opt = $("<option>").text(makePatientLabel(p)).data("patient", p);
            select.append(opt);
        }
    }

}

function makePatientLabel(patient){
    let patientIdRep = ("" + patient.patientId).padStart(4, "0");
    let birthday = kanjidate.sqldateToKanji(patient.birthday);
    return `(${patientIdRep}) ${patient.lastName}${patient.firstName} (${birthday}生)`;
}

function formatBirthday(birthday){
    let rep = kanjidate.sqldateToKanji(birthday);
    let age = kanjidate.calcAge(birthday);
    return `${rep}（${age}才）`;
}

