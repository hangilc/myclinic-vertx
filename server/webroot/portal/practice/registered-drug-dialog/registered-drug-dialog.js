import {Dialog} from "../dialog2.js";
import {parseElement} from "../../js/parse-element.js";
import {Search} from "./search.js";
import {Disp} from "./disp.js";

let bodyTemplate = `
    <div class="x-ele">
        <div class="x-disp-wrapper"></div>
        <div class="x-search-wrapper mt-2"></div>
    </div>
`;

let commandsTemplate = `
    <div class="d-flex justify-content-end">
        <button type="button" class="btn btn-secondary x-close">閉じる</button>
    </div>
`;

export class RegisteredDrugDialog extends Dialog {
    constructor(rest) {
        super();
        this.rest = rest;
        this.setDialogTitle("登録薬剤");
        let bodyMap = parseElement($(bodyTemplate));
        this.disp = (new Disp()).appendTo(bodyMap.dispWrapper);
        this.search = (new Search(rest)).appendTo(bodyMap.searchWrapper);
        this.search.onSelected(data => this.doSelected(data));
        this.appendToBody(bodyMap.ele);
        let commandsElement = $(commandsTemplate);
        let commandsMap = parseElement(commandsElement);
        commandsMap.close.on("click", event => this.close());
        this.appendToFooter(commandsElement);
    }

    doSelected(exampleFull){
        this.disp.add(exampleFull);
        this.disp.show();
    }

    focus(){
        this.search.focus();
    }
}