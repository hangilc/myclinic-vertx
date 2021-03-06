import {Dialog} from "../js/dialog2.js";
import {parseElement} from "../js/parse-node.js";
import {click} from "../js/dom-helper.js";
import {RegularDateInput} from "./date-input.js";

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
        this.setTitle("日付入力");
        this.getBody().innerHTML = bodyTmpl;
        let map = parseElement(this.getBody());
        this.dateInput = new RegularDateInput();
        map.dateInputWrapper.append(this.dateInput.ele);
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.enter, event => this.doEnter());
        click(fmap.cancel, event => this.close());
    }

    setDate(sqldate){
        this.dateInput.set(sqldate);
    }

    doEnter(){
        const errs = [];
        let date = this.dateInput.get(errs);
        if( errs.length > 0 ){
            alert(errs.join("\n"));
            return;
        }
        this.close(date);
    }
}