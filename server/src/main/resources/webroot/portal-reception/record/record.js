import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import * as TitleUtil from "../../js/title-util.js";
import * as TextUtil from "../../js/text-util.js";
import * as DrugUtil from "../../js/drug-util.js";
import * as ChargeUtil from "../../js/charge-util.js";
import {toZenkaku} from "../../js/jp-util.js";
import {ConductDisp} from "../../components/conduct-disp.js";

const tmpl = `
    <div>
        <div class="record-title mb-2 x-title"></div>
        <div class="row">
            <div class="col-6 x-texts"></div>
            <div class="col-6">
                <div class="x-hoken"></div>
                <div class="x-drugs"></div>
                <div class="x-shinryou-list"></div>
                <div class="x-conducts"></div>
                <div class="x-charge"></div>
            </div>
        </div>
    </div>
`;

const textTmpl = `
    <div></div>
`;

const drugTmpl = `
    <div>
        <span class="x-index"></span>
        <span class="x-label"></span>
    </div>
`;

const shinryouTmpl = `
    <div></div>
`;

export class Record {
    constructor(visitFull) {
        this.ele = createElementFrom(tmpl);
        const map = parseElement(this.ele);
        map.title.innerText = TitleUtil.titleRep(visitFull.visit.visitedAt);
        console.log(visitFull);
        visitFull.texts.forEach(text => {
            const e = createElementFrom(textTmpl);
            e.innerHTML = TextUtil.textRep(text.content);
            map.texts.append(e);
        });
        map.hoken.innerText = visitFull.hoken.rep || "（適用保険なし）";
        if( visitFull.drugs.length > 0 ){
            {
                const e = document.createElement("div");
                e.innerText = "Ｒｐ）";
                map.drugs.append(e);
            }
            let drugIndex = 1;
            visitFull.drugs.forEach(drugFull => {
                const indexRep = toZenkaku(`${drugIndex}）`);
                drugIndex += 1;
                const e = createElementFrom(drugTmpl);
                const m = parseElement(e);
                m.index.innerText = indexRep;
                m.label.innerText = DrugUtil.drugRep(drugFull);
                map.drugs.append(e);
            });
        }
        visitFull.shinryouList.forEach(shinryouFull => {
            const e = createElementFrom(shinryouTmpl);
            e.innerText = shinryouFull.master.name;
            map.shinryouList.append(e);
        });
        visitFull.conducts.forEach(conductFull => {
            const c = new ConductDisp(conductFull, true);
            map.conducts.append(c.ele);
        });
        map.charge.innerText = ChargeUtil.chargeRep(visitFull.charge);
    }
}