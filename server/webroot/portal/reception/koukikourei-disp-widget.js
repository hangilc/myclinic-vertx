import {Widget} from "./widget.js";
import {KoukikoureiDisp} from "./koukikourei-disp.js";
import * as kanjidate from "../js/kanjidate.js";

export class KoukikoureiDispWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.disp = new KoukikoureiDisp(map.disp);
        this.closeElement = map.close;
    }

    init(){
        super.init();
        this.closeElement.on("click", event => this.close());
        return this;
    }

    set(koukikourei){
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
