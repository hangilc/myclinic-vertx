import {Widget} from "./widget.js";
import {RoujinDisp} from "./roujin-disp.js";
import * as kanjidate from "../js/kanjidate.js";

export class RoujinDispWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.disp = new RoujinDisp(map.disp);
        this.closeElement = map.close;
    }

    init(){
        super.init();
        this.closeElement.on("click", event => this.close());
        return this;
    }

    set(roujin){
        super.set();
        let data = Object.assign({}, roujin, {
            validFrom: kanjidate.sqldateToKanji(roujin.validFrom),
            validUpto: kanjidate.sqldateToKanji(roujin.validUpto, {zeroValue: ""}),
            futanWari: futanWariRep(roujin.futanWari)
        });
        this.disp.set(data);
        return this;
    }
}

function futanWariRep(futanWari){
    return futanWari + "割";
}

