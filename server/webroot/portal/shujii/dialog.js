import {Component} from "./component.js";

export class Dialog extends Component {
    constructor(ele, map, rest) {
        super(map.dialog || ele, map, rest);
        this.dialogResult = null;
    }

    init(){
        this.ele.on("hidden.bs.modal", event => {
            if( this.dialogResolve ){
                this.dialogResolve(this.dialogResult);
            }
        });
    }

    set(){

    }

    setDialogResult(result){
        this.dialogResult = result;
    }

    hide(){
        this.ele.modal("hide");
    }

    close(result){
        this.setDialogResult(result);
        this.hide();
    }

    open(){
        return new Promise(resolve => {
            this.dialogResolve = resolve;
            this.ele.modal("show");
        });
    }
}