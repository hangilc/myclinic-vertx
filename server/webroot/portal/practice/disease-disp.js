import {Component} from "./component.js";
import {diseaseRep} from "../js/disease-util.js";

export class DiseaseDisp extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(diseaseFull){
        this.ele.text(diseaseRep(diseaseFull));
    }

}