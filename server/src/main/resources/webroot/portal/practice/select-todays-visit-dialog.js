import {Component} from "./component.js";

export class SelectTodaysVisitDialog extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.selectElement = map.select;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
        this.result = {mode: "cancel", data: null};
    }

    init(){
        this.selectElement.on("change", event => {
            this.result.data = this.selectElement.find("option:selected").data("data");
        });
        this.cancelElement.on("click", event => {
            this.result.data = null;
            this.ele.modal("hide");
        });
        this.enterElement.on("click", event => {
            if( this.result.data != null ) {
                this.result.mode = "selected";
                this.ele.modal("hide");
            }
        })
    }

    createOption(data){
        let patient = data.patient;
        let rep = `${patient.lastName}${patient.firstName}`;
        return $("<option>").text(rep).data("data", data);
    }

    async open(){
        this.selectElement.html("");
        let list = await this.rest.listTodaysVisits();
        list.forEach(data => {
            let opt = this.createOption(data);
            this.selectElement.append(opt);
        });
        return new Promise(resolve => {
            this.ele.on("hidden.bs.modal", event => {
                resolve(this.result);
            });
            this.ele.modal("show");
        })
    }
}

