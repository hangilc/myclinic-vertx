import {Component} from "../js/component.js";
import * as DiseaseUtil from "../js/disease-util.js";

export class DiseaseEnd extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.listElement = map.list;
        this.dateInputElement = map.dateInput;
    }

    init(){
        this.listElement.on("change", "input[type=checkbox]", event => this.doCheckChanged());
    }

    set(diseaseFulls){
        for(let df of diseaseFulls){
            let e = this.createCheckUnit(df);
            this.listElement.append(e);
        }
    }

    doCheckChanged(){
        let date = "";
        for(let df of this.checkedDiseases()){
            let startDate = df.disease.startDate;
            if( startDate > date ){
                date = startDate;
            }
        }
        this.dateInputElement.val(date);
    }

    createCheckUnit(diseaseFull){
        let e = $("<div>");
        e.append(this.createCheck(diseaseFull));
        let label = $("<span>", {
            class: "ml-1"
        });
        label.text(DiseaseUtil.diseaseRep(diseaseFull) + DiseaseUtil.datePart(diseaseFull.disease));
        e.append(label);
        return e;
    }

    createCheck(diseaseFull){
        let check = $("<input>", {
            type: "checkbox"
        });
        check.data("data", diseaseFull);
        return check;
    }

    checkedDiseases(){
        let checked = this.listElement.find("input[type=checkbox]:checked");
        let result = [];
        for(let i=0;i<checked.length;i++){
            let e = checked.slice(i, i+1);
            let df = e.data("data");
            result.push(df);
        }
        return result;
    }
}