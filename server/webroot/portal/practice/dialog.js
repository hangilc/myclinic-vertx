import {Component} from "./component.js";

export class Dialog extends Component {
    constructor(ele, map, rest) {
        super(map.dialog || ele, map, rest);
        this.dialogResult = null;
    }

    setDialogResult(result){
        this.dialogResult = result;
    }

    hide(){
        this.ele.modal("hide");
    }

    close(){
        this.hide();
    }

    open(){
        return new Promise(resolve => {
            this.ele.on("hidden.bs.modal", event => {
                this.ele.off("hidden.bs.modal");
                resolve(this.dialogResult);
            });
            this.ele.modal("show");
        });
    }
}