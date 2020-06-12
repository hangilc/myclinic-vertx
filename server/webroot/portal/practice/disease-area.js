import {Component} from "./component.js";

export class DiseaseArea extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.listElement = map.list;
        this.currentElement = map.current;
        this.addElement = map.add;
        this.endElement = map.end;
        this.editElement = map.edit;
    }

    init(diseaseFactory) {
        this.diseaseFactory = diseaseFactory;
    }

    set(diseaseFulls){
        this.listElement.html("");
        if( diseaseFulls ){
            for(let diseaseFull of diseaseFulls){
                this.addDisease(diseaseFull);
            }
            this.ele.removeClass("d-none");
        } else {
            this.ele.addClass("d-none");
        }
    }

    addDisease(diseaseFull){
        let disp = this.diseaseFactory.create(diseaseFull);
        disp.appendTo(this.listElement);
    }

}

