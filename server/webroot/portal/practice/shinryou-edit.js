import {Component} from "./component.js";

export class ShinryouEdit extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.labelElement = map.label;
        this.deleteElement = map.delete;
        this.cancelElement = map.cancel;
    }

    init(shinryouFull){
        this.labelElement.text(shinryouFull.master.name);
        this.deleteElement.on("click", async event => {
            await this.rest.deleteShinryou(shinryouFull.shinryou.shinryouId);
            this.ele.trigger("deleted");
        });
        this.cancelElement.on("click", event => this.ele.trigger("cancel"));
    }

    onCancel(cb){
        this.ele.on("cancel", cb);
    }

    onDeleted(cb){
        this.ele.on("deleted", cb);
    }
}