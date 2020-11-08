import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";

let tmpl = `
<div class="border border-info rounded p-2 mb-2">
    <h6 class="x-title mb-2">全保険リスト</h6>
    <div class="x-content mb-2" style="max-height:12rem; overflow-y: auto"></div>
    <div class="x-commands text-right">
        <button class="x-close btn btn-sm btn-secondary">閉じる</button>
    </div>
</div>
`;

export class AllHokenBox {
    constructor(patientId, rest){
        this.patientId = patientId;
        this.rest = rest;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.close.addEventListener("click", event => {
            this.ele.dispatchEvent(new Event("close"));
        });
    }

    getContent(){
        return this.map.content;
    }
}
