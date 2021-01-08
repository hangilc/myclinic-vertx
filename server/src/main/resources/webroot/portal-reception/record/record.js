import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import * as TitleUtil from "../../js/title-util.js";
import * as TextUtil from "../../js/text-util.js";

const tmpl = `
    <div>
        <div class="record-title mb-2 x-title"></div>
        <div class="row">
            <div class="col-6 x-texts"></div>
            <div class="col-6"></div>
        </div>
    </div>
`;

const textTmpl = `
    <div></div>
`;

export class Record {
    constructor(visitFull) {
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.title.innerText = TitleUtil.titleRep(visitFull.visit.visitedAt);
        visitFull.texts.forEach(text => {
            const e = createElementFrom(textTmpl);
            e.innerHTML = TextUtil.textRep(text.content);
            this.map.texts.append(e);
        });
    }
}