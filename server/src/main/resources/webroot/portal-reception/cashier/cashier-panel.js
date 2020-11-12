import {parseElement} from "../js/parse-node.js";

let tmpl = `
<div>
    <div class="mb-3 form-inline">
        <div class="h3">会計</div>
    </div>
    <div class="x-workarea pt-2"></div>
</div>
`;

export class CashierPanel {
    constructor(ele, rest) {
        ele.innerHTML = tmpl;
        this.rest = rest;
        this.map = parseElement(ele);
    }

    async reloadHook(){
        console.log("cashier reload");
    }
}