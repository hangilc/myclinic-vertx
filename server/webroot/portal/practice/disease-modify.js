import {Component} from "./component.js";
import {DiseaseSearch} from "./disease-search.js";
import * as DiseaseUtil from "../js/disease-util.js";

class Data {
    constructor(diseaseFull) {
        this.origDisease = diseaseFull.disease;
        this.byoumeiMaster = diseaseFull.master;
        this.adjMasters = diseaseFull.adjList.map(adj => adj.master);
        this.startDate = this.origDisease.startDate;
        this.endReason = this.origDisease.endReason;
        this.endDate = this.origDisease.endDate;
    }

    getName(){
        return DiseaseUtil.diseaseRepByMasters(this.byoumeiMaster, this.adjMasters);
    }

    getDisease(){
        let d = Object.assign({}, this.origDisease);
        d.startDate = this.startDate;
        d.endReason = this.endReason;
        d.endDate = this.endDate;
        return d;
    }

    getShuushokugocodes(){
        return this.adjMasters.map(m => m.shuushokugocode);
    }

    toReq(){
        return {
            disease: this.getDisease(),
            shuushokugocodes: this.getShuushokugocodes()
        };
    }
}


export class DiseaseModify extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.nameElement = map.name;
        this.startDateElement = map.startDate;
        this.endReasonSelectElement = map.endReasonSelect;
        this.endDateElement = map.endDate;
        this.search = new DiseaseSearch(map.search_, map.search, rest);
        this.data = null;
    }

    init(){
        this.search.init();
        this.search.onByoumeiMasterSelected((event, master) => {
            this.data.byoumeiMaster = master;
            this.setName(this.data.getName());
        });
        this.search.onShuushokugoMasterSelected((event, master) => {
            this.data.adjMasters.push(master);
            this.setName(this.data.getName());
        });
        this.startDateElement.on("change", event => this.doStartDateChanged());
    }

    set(diseaseFull){
        this.data = new Data(diseaseFull);
        this.setName(this.data.getName());
        this.setStartDate(this.data.startDate);
        this.setEndReason(this.data.endReason);
        this.setEndDate(this.data.endDate);
        this.search.set(this.data.startDate);
    }

    doStartDateChanged(){
        let startDate = this.startDateElement.val();
        this.setStartDate(startDate);
        this.search.set(startDate);
    }

    setName(name){
        this.nameElement.text(name);
    }

    setStartDate(startDate){
        this.startDateElement.val(startDate);
    }

    setEndReason(endReason){
        this.endReasonSelectElement.find(`option[value=${endReason}]`).prop("selected", true);
    }

    setEndDate(endDate){
        if( endDate === "0000-00-00" ){
            endDate = "";
        }
        this.endDateElement.val(endDate);
    }

}