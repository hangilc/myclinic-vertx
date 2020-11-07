import {Component} from "./component.js";
import * as consts from "../js/consts.js";
import * as DiseaseUtil from "../js/disease-util.js";

class Data {
    constructor() {
        this.byoumeiMaster = null;
        this.adjMasters = [];
        this.startDate = null;
    }

    getRep(){
        return DiseaseUtil.diseaseRepByMasters(this.byoumeiMaster, this.adjMasters);
    }

    clear(){
        this.byoumeiMaster = null;
        this.adjMasters = [];
        this.startDate = null;
    }

    setByoumeiMaster(master){
        this.byoumeiMaster = master;
    }

    addShuushokugoMaster(master){
        this.adjMasters.push(master);
    }

    clearShuushokugoMasters(){
        this.adjMasters = [];
    }

    setStartDate(startDate){
        this.startDate = startDate;
    }

    toRequest(patientId){
        if( patientId <= 0 ){
            return "No patientId";
        }
        if( !this.byoumeiMaster ){
            return "病名が指定されていません。";
        }
        if( !this.startDate ){
            return "開始日が指定されていません。";
        }
        let disease = {
            patientId,
            shoubyoumeicode: this.byoumeiMaster.shoubyoumeicode,
            startDate: this.startDate,
            endReason: consts.DiseaseEndReasonNotEnded,
            endDate: "0000-00-00"
        };
        let adjList = this.adjMasters.map(m => {
            return {shuushokugocode: m.shuushokugocode};
        });
        return {disease, adjList};
    }
}

class Example {
    constructor(example) {
        this.label = example.label;
        this.byoumei = example.byoumei;
        this.adjList = example.adjList;
    }

    getRep(){
        return this.label || this.byoumei;
    }
}

let examples = [];

export function initDiseaseExamples(configExamples){
    for(let ex of configExamples){
        examples.push(new Example(ex));
    }
}

export class DiseaseAdd extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.nameElement = map.name;
        this.searchFormElement = map.searchForm;
        this.dateInputElement = map.dateInput;
        this.enterElement = map.enter;
        this.suspElement = map.susp;
        this.delAdjElement = map.delAdj;
        this.exampleElement = map.example;
        this.searchTextElement = map.searchText;
        this.diseaseRadio = map.diseaseRadio;
        this.adjRadio = map.adjRadio;
        this.selectElement = map.select;
        this.data = new Data();
    }

    init(){
        this.enterElement.on("click", event => this.doEnter());
        this.searchFormElement.on("submit", event => { this.doSearch(); return false; });
        this.selectElement.on("change", event => this.doSelected());
        this.dateInputElement.on("change", event => this.data.setStartDate(this.dateInputElement.val()));
        this.exampleElement.on("click", event => this.doExample());
        this.suspElement.on("click", event => {
            this.data.addShuushokugoMaster(consts.suspMaster);
            this.setName(this.data.getRep());
        });
        this.delAdjElement.on("click", event => {
            this.data.clearShuushokugoMasters();
            this.setName(this.data.getRep());
        })
    }

    doExample(){
        this.selectElement.html("");
        for(let ex of examples){
            let opt = $("<option>");
            opt.text(ex.getRep());
            opt.data("kind", "example");
            opt.data("data", ex);
            this.selectElement.append(opt);
        }
    }

    set(patientId, date){
        this.patientId = patientId;
        this.date = date;
        this.dateInputElement.val(this.date);
        this.dateInputElement.trigger("change");
    }

    setName(s){
        this.nameElement.text(s);
    }

    async doEnter(){
        let req = this.data.toRequest(this.patientId);
        if( typeof req === "string" ){
            alert(req);
            return;
        }
        let diseaseId = await this.rest.enterDisease(req);
        let entered = await this.rest.getDisease(diseaseId);
        this.trigger("entered", entered);
        this.reset();
    }

    onEntered(cb){
        this.on("entered", (event, diseaseFull) => cb(event, diseaseFull));
    }

    async doExampleSelected(ex){
        let byoumei = ex.byoumei;
        if( byoumei ){
            let master = await this.rest.findByoumeiMasterByName(byoumei, this.date);
            if( !master ){
                alert(`傷病名（${byoumei}）を見つけられませんでした。`);
                return;
            }
            this.data.setByoumeiMaster(master);
        }
        if( ex.adjList ) {
            for (let adj of ex.adjList) {
                let master = await this.rest.findShuushokugoMasterByName(adj);
                if (!master) {
                    alert(`修飾語（${adj}）を見つけられませんでした。`);
                    return;
                }
                this.data.addShuushokugoMaster(master);
            }
        }
        this.setName(this.data.getRep());
    }

    async doSelected(){
        let opt = this.selectElement.find("option:selected");
        let kind = opt.data("kind");
        if( kind === "byoumei" ){
            let master = opt.data("master");
            this.data.setByoumeiMaster(master);
        } else if( kind === "adj" ){
            let master = opt.data("master");
            this.data.addShuushokugoMaster(master);
        } else if( kind === "example" ){
            let ex = opt.data("data");
            await this.doExampleSelected(ex);
        }
        this.setName(this.data.getRep());
    }

    reset(){
        this.data.clear();
        this.setName("");
        this.diseaseRadio.prop("checked", true);
        this.searchTextElement.val("");
        this.selectElement.html("");
        this.doExample();
    }

    async doSearch(){
        let text = this.searchTextElement.val();
        let date = this.date;
        if( this.diseaseRadio.is(":checked") ){
            let result = await this.rest.searchByoumeiMaster(text, date);
            this.selectElement.html("");
            for(let m of result){
                let opt = $("<option>");
                opt.text(m.name);
                opt.data("master", m);
                opt.data("kind", "byoumei");
                this.selectElement.append(opt);
            }
        } else {
            let result = await this.rest.searchShuushokugoMaster(text, date);
            this.selectElement.html("");
            for(let m of result){
                let opt = $("<option>");
                opt.text(m.name);
                opt.data("master", m);
                opt.data("kind", "adj");
                this.selectElement.append(opt);
            }
        }
    }
}