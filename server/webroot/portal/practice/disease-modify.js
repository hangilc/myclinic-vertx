import {Component} from "./component.js";
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
        this.data = null;
    }

    init(){

    }

    set(diseaseFull){
        this.data = new Data(diseaseFull);
        this.setName(this.data.getName());
        this.setStartDate(this.data.startDate);
        this.setEndReason(this.data.endReason);
        this.setEndDate(this.data.endDate);
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