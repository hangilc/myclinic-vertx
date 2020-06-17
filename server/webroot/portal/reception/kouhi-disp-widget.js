import {Widget} from "./widget.js";
import {KouhiDisp} from "./kouhi-disp.js";
import * as kanjidate from "../js/kanjidate.js";

export class KouhiDispWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.disp = new KouhiDisp(map.disp);
        this.closeElement = map.close;
    }

    init(){
        super.init();
        this.closeElement.on("click", event => this.close());
        return this;
    }

    set(kouhi){
        super.set();
        let data = Object.assign({}, kouhi, {
            validFrom: kanjidate.sqldateToKanji(kouhi.validFrom),
            validUpto: kanjidate.sqldateToKanji(kouhi.validUpto, {zeroValue: ""})
        });
        this.disp.set(data);
        return this;
    }
}
