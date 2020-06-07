import {Component} from "./component.js";

export class ShinryouRegularDialog extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.checksElement = map.checks;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
        this.result = {
            mode: "cancel",
            shinryouIds: null
        };
        this.resolver = result => { console.log("dummy resolver", result)};
    }

    init(visitId) {
        this.enterElement.on("click", async event => {
            let values = [];
            this.checksElement.find("input[type=checkbox]:checked")
                .toArray()
                .forEach(e => {
                    values.push($(e).val());
                });
            let batchEnterResult = await this.rest.batchEnterShinryouByNames(values, visitId);
            this.result.mode = "entered";
            this.result.shinryouIds = batchEnterResult.shinryouIds;
            this.result.drugIds = batchEnterResult.drugIds;
            this.result.conductIds = batchEnterResult.conductIds;
            this.ele.modal("hide");
        });
        this.cancelElement.on("click", event => this.ele.modal("hide"));
        this.ele.on("hidden.bs.modal", event => {
            this.resolver(this.result);
        });
    }

    async open() {
        this.checksElement.find("input[type=checkbox]:checked").prop("checked", false);
        return new Promise(resolve => {
            this.resolver = resolve;
            this.ele.modal("show");
        });
    }
}