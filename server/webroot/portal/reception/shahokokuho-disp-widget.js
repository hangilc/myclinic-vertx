import {Widget} from "./widget2.js";
import {ShahokokuhoDisp} from "./shahokokuho-disp.js";
import * as kanjidate from "../js/kanjidate.js";

let commandsTmpl = `
    <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
`;

export class ShahokokuhoDispWidget extends Widget {
    constructor(shahokokuho){
        super();
        this.setTitle("社保国保データ");
        let disp = new ShahokokuhoDisp(this.getContentElement());
        disp.set(shahokokuho);
        let cmap = this.setCommands(commandsTmpl);
        cmap.close.addEventListener("click", event => this.close());
        // this.disp = new ShahokokuhoDisp(map.disp);
        // this.closeElement = map.close;
    }

    init(){
        super.init();
        this.closeElement.on("click", event => this.close());
        return this;
    }

    set(shahokokuho){
        super.set();
        let data = Object.assign({}, shahokokuho, {
            honnin: honninRep(shahokokuho.honnin),
            validFrom: kanjidate.sqldateToKanji(shahokokuho.validFrom),
            validUpto: kanjidate.sqldateToKanji(shahokokuho.validUpto, {zeroValue: ""}),
            kourei: koureiRep(shahokokuho.kourei)
        });
        this.disp.set(data);
        return this;
    }
}

function honninRep(honnin){
    honnin = parseInt(honnin);
    switch(honnin){
        case 1: return "本人";
        case 0: return "家族";
        default: return honnin;
    }
}

function koureiRep(kourei){
    if( kourei === 0 ){
        return "高齢でない";
    } else {
        return kourei + "割";
    }
}


