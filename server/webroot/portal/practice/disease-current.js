import {Component} from "./component.js";
import * as DiseaseUtil from "../js/disease-util.js";

export class DiseaseCurrent extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(){

    }

    set(diseaseFulls){
        this.ele.html("");
        if( diseaseFulls ){
            for(let diseaseFull of diseaseFulls){
                let e = this.createLabel(diseaseFull);
                this.ele.append(e);
            }
        }
    }

    createLabel(diseaseFull){
        let e = $("<div>");
        e.text(DiseaseUtil.diseaseFullRep(diseaseFull));
        return e;
    }

}