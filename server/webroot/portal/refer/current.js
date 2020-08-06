import {Component} from "./component.js";
import * as kanjidate from "../js/kanjidate.js";
import {Prev} from "./prev.js";
import {SavedPdfWorkarea} from "./saved-pdf-workarea.js";

let suggestTemplate = `
    <a href="javascript:void(0)" class="dropdown-item"></a>
`;

export class Current extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.prev = new Prev(map.prev_, map.prev, rest);
        this.patientIdElement = map.patientId;
        this.nameElement = map.name;
        this.savedPdfWorkareaWrapper = map.savedPdfWorkareaWrapper;
        this.createElement = map.create;
        this.printElement = map.print;
        this.saveElement = map.save;
        this.referTitleControls = map.referTitleControls;
        this.referHospitalElement = map.referHospital;
        this.referDoctorElement = map.referDoctor;
        this.patientNameElement = map.patientName;
        this.patientInfoElement = map.patientInfo;
        this.diagnosisElement = map.diagnosis;
        this.contentElement = map.content;
        this.issueDateElement = map.issueDate;
        this.suggestDropdownItemsElement = map.suggestDropdownItems;
    }

    init(referList){
        super.init();
        this.prev.init();
        this.initSuggest(referList);
        this.createElement.on("click", event => this.doCreate());
        this.printElement.on("click", event => this.doPrint());
        this.saveElement.on("click", event => this.doSave());
        this.prev.onCopy(data => this.doCopy(data));
        this.prev.onDeleted(() => this.doDeleted());
        return this;
    }

    initSuggest(referList){
        this.suggestDropdownItemsElement.html("");
        for(let ref of referList){
            let a = $(suggestTemplate);
            let hospital = ref.hospital || "";
            let section = ref.section || "";
            let doctor = ref.doctor || "";
            let rep = [hospital, section, doctor].filter(a => a).join(" ");
            a.text(rep);
            a.on("click", event => this.doSuggest(hospital, section, doctor));
            this.suggestDropdownItemsElement.append(a);
        }
    }

    set(patient, prevs){
        super.set();
        if( patient ) {
            this.prev.set(patient.patientId, prevs);
        }
        this.patient = patient;
        this.patientIdElement.text(patient.patientId);
        this.nameElement.text(patient.lastName + patient.firstName);
        let issueDate = kanjidate.todayAsSqldate();
        this.issueDateElement.val(kanjidate.sqldateToKanji(issueDate));
        return this;
    }

    doSuggest(hospital, section, doctor){
        this.referHospitalElement.val(hospital);
        let doctorValue = [section, doctor].filter(a => a).join(" ");
        this.referDoctorElement.val(doctorValue);
    }

    doCopy(data){
        this.referHospitalElement.val(data.referHospital);
        this.referDoctorElement.val(data.referDoctor);
        this.diagnosisElement.val(data.diagnosis);
        this.contentElement.val(data.content);
    }

    getContent(){
        return this.contentElement.val();
    }

    getReferTitleInput(){
        return this.referTitleControls.find("input[type=radio][name=refer-title]:checked").val();
    }

    async compileData(){
        let data = { };
        data.title = this.getReferTitleInput();
        if( this.patient ){
            let patient = this.patient;
            data.patientName = `患者： ${patient.lastName}${patient.firstName} 様`;
            let birthday = kanjidate.sqldateToKanji(patient.birthday);
            let age = kanjidate.calcAge(patient.birthday);
            let sex = patient.sex === "M" ? "男" : "女";
            data.patientInfo = `${birthday}生 ${age}才 ${sex}性`;
        }
        let clinicInfo = await this.rest.getClinicInfo();
        data.referHospital = this.referHospitalElement.val();
        let doctorValue = this.referDoctorElement.val().trim();
        if( doctorValue === "" ){
            doctorValue = "　　　　　　　　"
        }
        if( !doctorValue.includes("先生") ){
            doctorValue += " 先生";
        }
        data.referDoctor = doctorValue;
        data.diagnosis = "診断： " + this.diagnosisElement.val();
        data.content = this.contentElement.val();
        data.issueDate = this.issueDateElement.val();
        data.clinicPostalCode = clinicInfo.postalCode;
        data.clinicAddress = clinicInfo.address;
        data.clinicPhone = "電話 " + clinicInfo.tel;
        data.clinicFax = "FAX " + clinicInfo.fax;
        data.clinicName = clinicInfo.name;
        data.doctorName = clinicInfo.doctorName;
        return data;
    }

    async doCreate(){
        if( this.patient ){
            let data = await this.compileData();
            let ops = await this.rest.referDrawer(data);
            let savePath = await this.rest.createTempFileName("refer-", ".pdf");
            await this.rest.saveDrawerAsPdf([ops], "A4", savePath, null);
            let workarea = new SavedPdfWorkarea(this.rest, savePath, this.patient.patientId);
            this.savedPdfWorkareaWrapper.append(workarea.ele);
        }
    }

    async doPrint(){
        let data = await this.compileData();
        let ops = await this.rest.referDrawer(data);
        await this.rest.printDrawer([ops]);
    }

    async doSave(){
        let patient = this.patient;
        if( patient ){
            let data = await this.compileData();
            let file = await this.rest.saveRefer(data, patient.patientId);
            await this.refreshPrev();
        }
    }

    async doDeleted(){
        await this.refreshPrev();
    }

    async refreshPrev(){
        let patient = this.patient;
        if( patient ){
            let prevs = await this.rest.listRefer(patient.patientId);
            this.prev.set(patient.patientId, prevs);
        }
    }
}
