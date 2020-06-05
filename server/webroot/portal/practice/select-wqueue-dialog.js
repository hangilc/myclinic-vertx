import {Component} from "./component.js";

export class SelectWqueueDialog extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.selectElement = map.select;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
        this.result = {mode: "cancel", data: null};
    }

    init(stateConverter){
        this.stateConverter = stateConverter;
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

    createOption(wqueueFull){
        let stateRep = this.stateConverter(wqueueFull.wqueue.waitState);
        let patient = wqueueFull.patient;
        let rep = `[${stateRep}] ${patient.lastName}${patient.firstName}`;
        return $("<option>").text(rep).data("data", wqueueFull);
    }

    async open(){
        this.selectElement.html("");
        let list = await this.rest.listWqueueFull();
        list.forEach(wqueueFull => {
            let opt = this.createOption(wqueueFull);
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

