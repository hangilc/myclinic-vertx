import {Component} from "./component.js";
import * as DiseaseUtil from "../js/disease-util.js";
import * as consts from "../js/consts.js";

export class DiseaseDisp extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(){
    }

    set(diseaseFull){
        this.diseaseFull = diseaseFull;
        let name = DiseaseUtil.diseaseRep(diseaseFull);
        let startDate = diseaseFull.disease.startDate;
        let startDateRep = DiseaseUtil.formatDate(startDate);
        let endReason = diseaseFull.disease.endReason;
        if( endReason === consts.DiseaseEndReasonNotEnded ){
            this.ele.text(`${name} (${startDateRep})`);
        } else {
            let endDate = diseaseFull.disease.endDate;
            let endDateRep = DiseaseUtil.formatDate(endDate);
            this.ele.text(`${name} (${startDateRep}-${endDateRep})`);
        }
    }

}