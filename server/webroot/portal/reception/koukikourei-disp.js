import {parseElement} from "../js/parse-node.js";
import * as kanjidate from "../js/kanjidate.js";

let tmpl = `
    <div class="row">
        <div class="col-sm-2 d-flex justify-content-end">保険者番号</div>
        <div class="col-sm-10 x-hokensha-bangou"></div>
        <div class="col-sm-2 d-flex justify-content-end">被保険者番号</div>
        <div class="col-sm-10 x-hihokensha-bangou"></div>
        <div class="col-sm-2 d-flex justify-content-end">開始日</div>
        <div class="col-sm-10 x-valid-from"></div>
        <div class="col-sm-2 d-flex justify-content-end">終了日</div>
        <div class="col-sm-10 x-valid-upto"></div>
        <div class="col-sm-2 d-flex justify-content-end">負担割</div>
        <div class="col-sm-10 x-futan-wari"></div>
    </div>
`;

export class KoukikoureiDisp {
    constructor(koukikourei, ele) {
        if( !ele ){
            let wrapper = document.createElement("div");
            wrapper.innerHTML = tmpl;
            ele = wrapper.firstChild;
        }
        if( ele.children && ele.children.length === 0 ){
            ele.innerHTML = tmpl;
        }
        this.ele = ele;
        let map = parseElement(ele);
        this.hokenshaBangouElement = map.hokenshaBangou;
        this.hihokenshaBangouElement = map.hihokenshaBangou;
        this.validFromElement = map.validFrom;
        this.validUptoElement = map.validUpto;
        this.futanWariElement = map.futanWari;
        this._set(koukikourei);
    }

    _set(koukikourei){
        this.hokenshaBangouElement.innerText = koukikourei.hokenshaBangou;
        this.hihokenshaBangouElement.innerText = koukikourei.hihokenshaBangou;
        this.validFromElement.innerText = dateRep(koukikourei.validFrom);
        this.validUptoElement.innerText = dateRep(koukikourei.validUpto);
        this.futanWariElement.innerText = futanWariRep(koukikourei.futanWari);
    }
}

function dateRep(sqldate){
    return kanjidate.sqldateToKanji(sqldate, {
        zeroValue: "（なし）",
        padZero: true
    });
}

function futanWariRep(futanWari){
    return `${futanWari}割`;
}
