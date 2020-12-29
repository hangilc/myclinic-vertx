import {createElementFrom} from "../../../js/create-element-from.js";
import {shohousenTextContentDataToDisp} from "../funs.js";

let tmpl = `
    <div class="my-1"></div>
`;

export class TextDisp  {
    constructor(text) {
        this.ele = createElementFrom(tmpl);
        let content = shohousenTextContentDataToDisp(text.content);
        this.ele.innerHTML = content.replace(/\r\n|\n|\r/g, "<br/>\n");
        this.ele.addEventListener("click", event => this.ele.dispatchEvent(new Event("start-edit")));
    }
}