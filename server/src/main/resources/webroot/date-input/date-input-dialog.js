import {Dialog} from "../js/dialog2.js";
import {parseElement} from "../js/parse-node.js";
import {click} from "../js/dom-helper.js";
import {RegularDateInput} from "./date-input.js";
import {alertAndReturnUndefined} from "../js/result.js";

let bodyTmpl = `
    <div class="mb-2 x-date-input-wrapper"></div>
`;

let footerTmpl = `
    <button class="btn btn-primary x-enter">入力</button>
    <button class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class DateInputDialog extends Dialog {
    constructor() {
        super();
        this.resultHandler = alertAndReturnUndefined;
        this.setTitle("日付入力");
        this.getBody().innerHTML = bodyTmpl;
        let bmap = parseElement(this.getBody());
        let dateInput = this.dateInput = new RegularDateInput();
        bmap.dateInputWrapper.append(dateInput.ele);
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.enter, event => this.doEnter());
        click(fmap.cancel, event => this.close());
    }

    setDate(sqldate){
        this.dateInput.set(sqldate);
    }

    doEnter(){
        let date = this.dateInput.get(this.resultHandler);
        if( date !== undefined ){
            this.close(date);
        }
    }
}