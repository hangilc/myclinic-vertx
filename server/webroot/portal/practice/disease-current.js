import {Component} from "./component.js";

export class DiseaseCurrent extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(diseaseDispFactory){
        this.diseaseDispFactory = diseaseDispFactory;
    }

    set(diseaseFulls){
        this.ele.html("");
        if( diseaseFulls ){
            for(let diseaseFull of diseaseFulls){
                let compDisp = this.diseaseDispFactory.create(diseaseFull);
                compDisp.appendTo(this.ele);
            }
        }
    }

}