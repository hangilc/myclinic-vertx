import {Widget} from "./widget2.js";
import {KoukikoureiDisp} from "./koukikourei-disp.js";
import * as kanjidate from "../js/kanjidate.js";

let cTmpl = `
    <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
`;

export class KoukikoureiDispWidget extends Widget {
    constructor(koukikourei){
        super();
        this.setTitle("後期高齢保険データ");
        this.disp = new KoukikoureiDisp(koukikourei, this.getContentElement());
        let cmap = this.setCommands(cTmpl);
        cmap.close.addEventListener("click", event => this.close());
    }

    _set(koukikourei){
        super.set();
        let data = Object.assign({}, koukikourei, {
            validFrom: kanjidate.sqldateToKanji(koukikourei.validFrom),
            validUpto: kanjidate.sqldateToKanji(koukikourei.validUpto, {zeroValue: ""}),
            futanWari: futanWariRep(koukikourei.futanWari)
        });
        this.disp.set(data);
        return this;
    }
}

function futanWariRep(futanWari){
    return futanWari + "割";
}
