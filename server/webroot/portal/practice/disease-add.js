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
            endDate: null
        };
        let adjList = this.adjMasters.map(m => m.shuushokugocode);
        return {disease, adjList};
    }
}

export class DiseaseAdd extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.nameElement = map.name;
        this.dateInputElement = map.dateInput;
        this.enterElement = map.enter;
        this.searchTextElement = map.searchText;
        this.searchButtonElement = map.searchButton;
        this.diseaseRadio = map.diseaseRadio;
        this.adjRadio = map.adjRadio;
        this.selectElement = map.select;
        this.data = new Data();
    }

    init(){
        this.enterElement.on("click", event => this.doEnter());
        this.searchButtonElement.on("click", event => this.doSearch());
        this.selectElement.on("change", event => this.doSelected());
        this.dateInputElement.on("change", event => this.data.setStartDate(this.dateInputElement.val()));
    }

    set(patientId, date){
        this.patientId = patientId;
        this.date = date;
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
        console.log(req);
    }

    doSelected(){
        let opt = this.selectElement.find("option:selected");
        let kind = opt.data("kind");
        if( kind === "byoumei" ){
            let master = opt.data("master");
            this.data.setByoumeiMaster(master);
        } else if( kind === "adj" ){
            let master = opt.data("master");
            this.data.addShuushokugoMaster(master);
        }
        this.setName(this.data.getRep());
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