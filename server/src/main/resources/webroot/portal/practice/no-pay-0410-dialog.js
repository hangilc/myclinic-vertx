import {Dialog} from "../../js/dialog.js";
import * as kanjidate from "../../js/kanjidate.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";

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

let bodyTmpl = `
    <div class="x-items"></div>
`;

let footerTmpl = `
    <button class="btn btn-primary x-no-pay">未収に</button>
    <button class="btn btn-secondary x-close">閉じる</button>
`;

export class NoPay0410Dialog extends Dialog {

    constructor(prop, visits){
        super();
        this.setTitle("遠隔診療未収");
        this.getBody().innerHTML = bodyTmpl;
        let bmap = parseElement(this.getBody());
        for(let visit of visits){
            let item = new Item(visit);
            bmap.items.append(item.ele);
        }
        this.getFooter().innerHTML = footerTmpl;
        let cmap = parseElement(this.getFooter());
        this.cmap.noPay.addEventListener("click", async event => {
            let paytime = kanjidate.nowAsSqldatetime();
            await prop.rest.batchEnterPayment(visits.map(visit => ({
                visitId: visit.visitId,
                amount: 0,
                paytime
            })));
            this.close({
                result: "no-pay",
                visitIds: visits.map(visit => visit.visitId)
            });
        });
        this.cmap.close.addEventListener("click", event => this.close({result: "close"}));
    }

}