import {Component} from "./component.js";
import * as DiseaseUtil from "../js/disease-util.js";
import * as kanjidate from "../js/kanjidate.js";
import * as consts from "../js/consts.js";

export class DiseasePanel extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.nameElement = map.name;
        this.startDateElement = map.startDate;
        this.endReasonElement = map.endReason;
        this.endDateElement = map.endDate;
    }

    init(){

    }

    set(diseaseFull){
        this.nameElement.text(DiseaseUtil.diseaseRep(diseaseFull));
        this.startDateElement.text(this.formatDate(diseaseFull.disease.startDate));
        this.endReasonElement.text(this.formatEndReason(diseaseFull.disease.endReason));
        this.endDateElement.text(this.formatDate(diseaseFull.disease.endDate));
    }

    formatDate(date){
        if( !date ) {
            return "";
        } else if( date === "0000-00-00" ){
            return "";
        } else {
            let data = kanjidate.sqldateToData(date);
            return `${data.gengou.name}${data.nen}年${data.month}月${data.day}日`;
        }
    }

    formatEndReason(endReason){
        return consts.diseaseEndReasonToKanji(endReason);
    }
}