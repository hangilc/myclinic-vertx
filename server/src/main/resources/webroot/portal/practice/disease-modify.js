import {Component} from "./component.js";
import {DiseaseSearch} from "./disease-search.js";
import * as DiseaseUtil from "../js/disease-util.js";
import * as consts from "../js/consts.js";

class Data {
    constructor(diseaseFull) {
        this.origDisease = diseaseFull.disease;
        this.byoumeiMaster = diseaseFull.master;
        this.adjMasters = diseaseFull.adjList.map(adj => adj.master);
        this.startDate = this.origDisease.startDate;
        this.endReason = this.origDisease.endReason;
        this.endDate = this.origDisease.endDate;
    }

    getDiseaseId(){
        return this.origDisease.diseaseId;
    }

    clearAdjMasters(){
        this.adjMasters = [];
    }

    getName(){
        return DiseaseUtil.diseaseRepByMasters(this.byoumeiMaster, this.adjMasters);
    }

    getDisease(){
        let d = Object.assign({}, this.origDisease);
        d.shoubyoumeicode = this.byoumeiMaster.shoubyoumeicode;
        d.startDate = this.startDate;
        d.endReason = this.endReason;
        d.endDate = this.endDate;
        if( d.endReason === "N" ){
            d.endDate = "0000-00-00";
        }
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
        this.enterElement = map.enter;
        this.suspElement = map.susp;
        this.delAdjElement = map.delAdj;
        this.clearEndDateElement = map.clearEndDate;
        this.deleteElement = map.delete;
        this.search = new DiseaseSearch(map.search_, map.search, rest);
        this.data = null;
    }

    init(examples){
        this.search.init(examples);
        this.search.onByoumeiMasterSelected((event, master) => {
            this.data.byoumeiMaster = master;
            this.setName(this.data.getName());
        });
        this.search.onShuushokugoMasterSelected((event, master) => {
            this.data.adjMasters.push(master);
            this.setName(this.data.getName());
        });
        this.startDateElement.on("change", event => this.doStartDateChanged());
        this.endReasonSelectElement.on("change", event => this.doEndReasonChanged());
        this.endDateElement.on("change", event => this.doEndDateChanged());
        this.enterElement.on("click", event => this.doEnter());
        this.suspElement.on("click", event => {
            this.data.adjMasters.push(consts.suspMaster);
            this.setName(this.data.getName());
        });
        this.delAdjElement.on("click", event => {
            this.data.clearAdjMasters();
            this.setName(this.data.getName());
        });
        this.clearEndDateElement.on("click", event => {
            this.data.endDate = "0000-00-00";
            this.endDateElement.val("");
        });
        this.deleteElement.on("click", event => this.doDelete());
    }

    set(diseaseFull){
        this.data = new Data(diseaseFull);
        this.setName(this.data.getName());
        this.setStartDate(this.data.startDate);
        this.setEndReason(this.data.endReason);
        this.setEndDate(this.data.endDate);
        this.search.set(this.data.startDate);
    }

    async doDelete(){
        if( confirm("この病名を削除していいですか？") ){
            await this.rest.deleteDisease(this.data.getDiseaseId());
            this.trigger("modified");
        }
    }

    onModified(cb){
        this.on("modified", cb);
    }

    async doEnter(){
        let req = this.data.toReq();
        await this.rest.modifyDisease(req);
        this.trigger("modified");
    }

    doStartDateChanged(){
        let startDate = this.startDateElement.val();
        this.data.startDate = startDate;
        this.setStartDate(startDate);
        this.search.set(startDate);
    }

    getEndReason(){
        return this.endReasonSelectElement.find("option:selected").val();
    }

    doEndReasonChanged(){
        this.data.endReason = this.getEndReason();
    }

    doEndDateChanged(){
        let endDate = this.endDateElement.val();
        this.data.endDate = endDate;
        this.setEndDate(endDate);
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