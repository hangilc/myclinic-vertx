import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";

let tmpl = `
    <div class="mb-2 border rounded p-2">
        <div class="h5">未収リスト</div>
        <div class="x-list"></div>
        <div class="x-commands"></div>
    </div>
`;

let itemTmpl = `
    <div>
        <span class="x-date"></span>
    </div>
`;

class Item {
    constructor(visit){
        this.ele = createElementFrom(itemTmpl);
        this.map = parseElement(this.ele);
        this.map.date.innerText = kanjidate.sqldateToKanji(visit.visitedAt.substring(0, 10));
    }
}

export class NoPayList {
    constructor(prop){
        this.prop = prop;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
    }

    async add(visitId){
        let visit = await this.prop.rest.getVisit(visitId);
        let item = new Item(visit);
        this.map.list.append(item.ele);
    }
}