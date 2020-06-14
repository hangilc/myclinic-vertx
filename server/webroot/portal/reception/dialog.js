import {Component} from "../js/component.js";
import {parseElement} from "../js/parse-element.js";

let html = `
<div class="modal x-dialog_" tabindex="-1" role="dialog" data-backdrop="true">
    <div class="modal-dialog" role="document">
    <div class="modal-content">
    <div class="modal-header">
    <h5 class="modal-title"></h5>
    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
    <span aria-hidden="true">&times;</span>
</button>
</div>
<div class="modal-body"></div>
    <div class="modal-footer"></div>
    </div>
    </div>
    </div>
`;


export class Dialog extends Component {
    constructor(rest) {
        let ele = $(html);
        let map = parseElement(ele);
        super(ele, map, rest);
        this.dialogTitleElement = this.ele.find(".modal-title");
        this.dialogBodyElement = this.ele.find(".modal-body");
        this.dialogFooterElement = this.ele.find(".modal-footer");
    }

    init(dialogTitle, bodyComponent, footerComponent){
        this.dialogTitleElement.text(dialogTitle);
        if( bodyComponent ){
            bodyComponent.appendTo(this.dialogBodyElement);
        }
        if( footerComponent ){
            footerComponent.appendTo(this.dialogFooterElement);
        }
    }

    set(){

    }

    async open(){
        return new Promise(resolve => {
            this.ele.modal("show");
        });
    }
}

export function createDialog(rest, title, body, footer){
    let dialog = new Dialog(rest);
    dialog.init(title, body, footer);
    dialog.set();
    if( body && typeof body.setDialog === "function" ){
        body.setDialog(dialog);
    }
    if( footer && typeof footer.setDialog === "function" ){
        footer.setDialog(dialog);
    }
    return dialog;
}