import {Component} from "./component.js";
import * as kanjidate from "../js/kanjidate.js";

export class DiseaseArea extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.workareaElement = map.workarea;
        this.currentElement = map.current;
        this.addElement = map.add;
        this.endElement = map.end;
        this.editElement = map.edit;
    }

    init(diseaseCurrentFactory, diseaseAddFactory, diseaseEndFactory, diseaseEditFactory,
         diseaseModifyFactory, diseaseExamples) {
        this.diseaseCurrentFactory = diseaseCurrentFactory;
        this.diseaseAddFactory = diseaseAddFactory;
        this.diseaseEndFactory = diseaseEndFactory;
        this.diseaseEditFactory = diseaseEditFactory;
        this.diseaseModifyFactory = diseaseModifyFactory;
        this.diseaseExamples = diseaseExamples;
        this.currentElement.on("click", event => this.current());
        this.addElement.on("click", event => this.add());
        this.endElement.on("click", event => this.end());
        this.editElement.on("click", event => this.edit());
    }

    set(patientId, diseaseFulls){
        this.patientId = patientId;
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
        comp.onClicked((event, df) => this.modify(df));
        this.workareaElement.html("");
        comp.appendTo(this.workareaElement);
    }

    add(){
        let comp = this.diseaseAddFactory.create(this.patientId, kanjidate.todayAsSqldate());
        this.workareaElement.html("");
        comp.onEntered((event, entered) => this.diseaseFulls.push(entered));
        comp.doExample();
        comp.appendTo(this.workareaElement);
    }

    end(){
        let comp = this.diseaseEndFactory.create(this.diseaseFulls);
        this.workareaElement.html("");
        comp.appendTo(this.workareaElement);
    }

    async doDiseaseModified(){
        if( this.patientId > 0 ){
            this.diseaseFulls = await this.rest.listCurrentDisease(this.patientId);
            this.current();
        }
    }

    async edit(){
        let patientId = this.patientId;
        if( patientId > 0 ){
            let dfs = await this.rest.listDisease(patientId);
            let comp = this.diseaseEditFactory.create(dfs);
            comp.onEdit((event, df) => this.modify(df));
            this.workareaElement.html("");
            comp.appendTo(this.workareaElement);
        }
    }

    modify(diseaseFull){
        let compModify = this.diseaseModifyFactory.create(diseaseFull, this.diseaseExamples);
        compModify.onModified(event => this.doDiseaseModified());
        this.workareaElement.html("");
        compModify.appendTo(this.workareaElement);
    }

}

