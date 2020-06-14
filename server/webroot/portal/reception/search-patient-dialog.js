import {Dialog} from "./dialog.js";

export class SearchPatientDialog extends Dialog {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(){
        super.init("患者検索");
    }

    set(){
        super.set();
    }
}