import {Component} from "./component.js";

export class SelectPreviousVisitDialog extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.selectElement = map.select;
        this.dateElement = map.date;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
        this.result = {mode: "cancel", data: null};
    }

    init(){
        this.dateElement.on("change", event => {
            let date = this.dateElement.val();
            if( date ){
                let list = await this.rest.listTodaysVisits();
                list.forEach(wqueueFull => {
                    let opt = this.createOption(wqueueFull);
                    this.selectElement.append(opt);
                });
            }
        });
        this.selectElement.on("change", event => {
            this.result.data = this.selectElement.find("option:selected").data("data");
        });
        this.cancelElement.on("click", event => {
            this.result.data = null;
            this.ele.modal("hide");
        });
        this.enterElement.on("click", event => {
            this.result.mode = "selected";
            this.ele.modal("hide");
        })
    }

    createOption(data){
        let patient = data.patient;
        let rep = `${patient.lastName}${patient.firstName}`;
        return $("<option>").text(rep).data("data", data);
    }

    async open(){
        this.selectElement.html("");
        return new Promise(resolve => {
            this.ele.on("hidden.bs.modal", event => {
                resolve(this.result);
            });
            this.ele.modal("show");
        })
    }
}

