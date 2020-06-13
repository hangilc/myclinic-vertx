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

    onClicked(cb){
        this.on("clicked", (event, diseaseFull) => cb(event, diseaseFull));
    }

    createLabel(diseaseFull){
        let e = $("<div>");
        e.text(DiseaseUtil.diseaseFullRep(diseaseFull));
        e.on("click", event => this.trigger("clicked", diseaseFull));
        return e;
    }

}