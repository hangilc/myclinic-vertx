import {parseElement} from "../js/parse-node.js";
import * as kanjidate from "../js/kanjidate.js";

let tmpl = `
    <div class="row">
        <div class="col-sm-2 d-flex justify-content-end">市町村番号</div>
        <div class="col-sm-10 x-shichouson"></div>
        <div class="col-sm-2 d-flex justify-content-end">受給者番号</div>
        <div class="col-sm-10 x-jukyuusha"></div>
        <div class="col-sm-2 d-flex justify-content-end">開始日</div>
        <div class="col-sm-10 x-valid-from"></div>
        <div class="col-sm-2 d-flex justify-content-end">終了日</div>
        <div class="col-sm-10 x-valid-upto"></div>
        <div class="col-sm-2 d-flex justify-content-end">負担割</div>
        <div class="col-sm-10 x-futan-wari"></div>
    </div>
`;

export class RoujinDisp {
    constructor(ele) {
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
        this.shichousonElement = map.shichouson;
        this.jukyuushaElement = map.jukyuusha;
        this.validFromElement = map.validFrom;
        this.validUptoElement = map.validUpto;
        this.futanWariElement = map.futanWari;
    }

    set(roujin){
        this.shichousonElement.innerText = roujin.shichouson;
        this.jukyuushaElement.innerText = roujin.jukyuusha;
        this.validFromElement.innerText = dateRep(roujin.validFrom);
        this.validUptoElement.innerText = dateRep(roujin.validUpto);
        this.futanWariElement.innerText = futanWariRep(roujin.futanWari);
        return this;
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
