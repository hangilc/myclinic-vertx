import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";

let tmpl = `
    <div class="my-1"></div>
`;

export class TextDisp  {
    constructor(text) {
        this.ele = createElementFrom(tmpl);
        let content = text.content;
        if( content.startsWith("院外処方") ){
            content = content.replace(/　/g, " "); // replace zenkaku space to ascii space
        }
        this.ele.innerHTML = content.replace(/\r\n|\n|\r/g, "<br/>\n");
    }
}