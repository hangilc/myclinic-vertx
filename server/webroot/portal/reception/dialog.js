import {Component} from "../js/component.js";

export class Dialog extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.dialogResult = null;
    }

    init(){
        
    }

    set(){

    }

    onOpened(cb){
        this.ele.on("shown.bs.modal", event => cb());
    }

    setDialogResult(result){
        this.dialogResult = result;
    }

    close(result){
        this.ele.modal("hide");
    }

    async open(){
        return new Promise(resolve => {
            this.ele.on("hidden.bs.modal", event => resolve(this.dialogResult));
            this.ele.modal("show");
        });
    }
}

