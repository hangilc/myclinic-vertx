import {Component} from "./component.js";

export class DiseaseArea extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.workareaElement = map.workarea;
        this.currentElement = map.current;
        this.addElement = map.add;
        this.endElement = map.end;
        this.editElement = map.edit;
    }

    init(diseaseCurrentFactory) {
        this.diseaseCurrentFactory = diseaseCurrentFactory;
    }

    set(diseaseFulls){
        this.diseaseFulls = diseaseFulls;
        this.workareaElement.html("");
        if( diseaseFulls ){
            this.ele.removeClass("d-none");
        } else {
            this.ele.addClass("d-none");
        }
    }

    current(){
        let comp = this.diseaseCurrentFactory.create(this.diseaseFulls);
        this.workareaElement.html("");
        comp.appendTo(this.workareaElement);
    }

}

