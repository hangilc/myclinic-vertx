import {Dialog} from "../js/dialog2.js";
import {parseElement} from "../js/parse-node.js";

let token = null;

export class CalloutDialog extends Dialog {
    constructor(number, rest){
        super();
        this.number = number;
        this.rest = rest;
    }

    async init(){
        if( token == null ){
            token = await this.rest.twilioWebphoneToken();
        }
    }
}