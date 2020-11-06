import {Component} from "../js/component.js";
import * as DiseaseUtil from "../js/disease-util.js";
import {DiseasePanel} from "./disease-panel.js";

export class DiseaseEdit extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.selectElement = map.select;
        this.panelElement= map.panel_;
        this.panelMap = map.panel;
        this.editElement = map.edit;
        this.data = null;
    }

    init(){
        this.selectElement.on("change", event => this.doSelectionChanged());
        this.panel = new DiseasePanel(this.panelElement, this.panelMap, this.rest);
        this.editElement.on("click", event => this.doEdit());
        this.panel.init();
    }

    set(diseaseFulls){
        this.selectElement.html("");
        if( diseaseFulls ){
            for(let df of diseaseFulls){
                this.selectElement.append(this.createOpt(df));
            }
        }
    }

    onEdit(cb){
        this.on("edit", (event, diseaseFull) => cb(event, diseaseFull));
    }

    doEdit(){
        if( this.data ){
            this.trigger("edit", this.data);
        }
    }

    doSelectionChanged(){
        let sel = this.selectElement.find("option:selected");
        let data = sel.data("data");
        this.panel.set(data);
        this.data = data;
    }

    createOpt(diseaseFull){
        let opt = $("<option>");
        opt.text(DiseaseUtil.diseaseFullRep(diseaseFull));
        opt.data("data", diseaseFull);
        return opt;
    }

}