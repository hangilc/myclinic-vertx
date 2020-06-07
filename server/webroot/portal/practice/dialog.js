import {Component} from "./component.js";

export class Dialog extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.result = {};
        this.resolver = result => console.log("dialog result", result);
        ele.on("hidden.bs.modal", event => {
            this.resolver(this.result);
        });
    }

    setResult(result){
        this.result = result;
    }

    hide(){
        this.ele.modal("hide");
    }

    async open(){
        return new Promise(resolve => {
            this.resolver = resolve;
            this.ele.modal("show");
        });
    }
}