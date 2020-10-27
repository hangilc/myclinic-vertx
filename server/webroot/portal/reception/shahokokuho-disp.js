import {parseElement} from "../js/parse-node.js";
import * as kanjidate from "../js/kanjidate.js";

let tmpl = `
    <div class="row">
        <div class="col-sm-2 d-flex justify-content-end">保険者番号</div>
        <div class="col-sm-10 x-hokensha-bangou"></div>
        <div class="col-sm-2 d-flex justify-content-end">被保険者記号</div>
        <div class="col-sm-10 x-hihokensha-kigou"></div>
        <div class="col-sm-2 d-flex justify-content-end">被保険者番号</div>
        <div class="col-sm-10 x-hihokensha-bangou"></div>
        <div class="col-sm-2 d-flex justify-content-end">本人・家族</div>
        <div class="col-sm-10 x-honnin"></div>
        <div class="col-sm-2 d-flex justify-content-end">開始日</div>
        <div class="col-sm-10 x-valid-from"></div>
        <div class="col-sm-2 d-flex justify-content-end">終了日</div>
        <div class="col-sm-10 x-valid-upto"></div>
        <div class="col-sm-2 d-flex justify-content-end">高齢</div>
        <div class="col-sm-10 x-kourei"></div>
    </div>
`;

export class ShahokokuhoDisp {
    constructor(ele) {
        if( !ele ){
            let wrapper = document.createElement("div");
            wrapper.innerHTML = tmpl;
            ele = wrapper.firstChild;
        }
        if( ele.children && ele.children.length === 0 ){
            ele.innerHTML = tmpl;
        }
        let map = parseElement(ele);
        this.hokenshaBangouElement = map.hokenshaBangou;
        this.hihokenshaKigouElement = map.hihokenshaKigou;
        this.hihokenshaBangouElement = map.hihokenshaBangou;
        this.honninElement = map.honnin;
        this.validFromElement = map.validFrom;
        this.validUptoElement = map.validUpto;
        this.koureiElement = map.kourei;
    }

    set(shahokokuho){
        if( shahokokuho ){
            this.hokenshaBangouElement.innerText = shahokokuho.hokenshaBangou;
            this.hihokenshaKigouElement.innerText = shahokokuho.hihokenshaKigou;
            this.hihokenshaBangouElement.innerText = shahokokuho.hihokenshaBangou;
            this.honninElement.innerText = honninRep(shahokokuho.honnin);
            this.validFromElement.innerText = dateRep(shahokokuho.validFrom);
            this.validUptoElement.innerText = dateRep(shahokokuho.validUpto);
            this.koureiElement.innerText = koureiRep(shahokokuho.kourei);
        } else {
            this.hokenshaBangouElement.innerText = "";
            this.hihokenshaKigouElement.innerText = "";
            this.hihokenshaBangouElement.innerText = "";
            this.honninElement.innerText = "";
            this.validFromElement.innerText = "";
            this.validUptoElement.innerText = "";
            this.koureiElement.innerText = "";
        }
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

function dateRep(sqldate){
    return kanjidate.sqldateToKanji(sqldate, {
        zeroValue: "（なし）",
        padZero: true
    });
}

