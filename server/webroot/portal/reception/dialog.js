import {Component} from "../js/component.js";

export class Dialog extends Component {
    constructor(ele, map, rest) {
        super(map.dialog_, map.dialog, rest);
        this.dialogTitleElement = this.ele.find(".modal-title");
        this.dialogBodyElement = this.ele.find(".modal-body");
        this.dialogFooterElement = this.ele.find(".modal-footer");
    }

    init(dialogTitle){
        this.dialogTitleElement.text(dialogTitle);
    }

    set(){

    }

    async open(){
        return new Promise(resolve => {
            this.ele.modal("show");
        });
    }
}