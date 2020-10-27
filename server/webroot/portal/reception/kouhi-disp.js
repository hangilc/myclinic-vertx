import {parseElement} from "../js/parse-node.js";
import * as kanjidate from "../js/kanjidate.js";

let tmpl = `
    <div class="row">
        <div class="col-sm-2 d-flex justify-content-end">負担者番号</div>
        <div class="col-sm-10 x-futansha"></div>
        <div class="col-sm-2 d-flex justify-content-end">受給者番号</div>
        <div class="col-sm-10 x-jukyuusha"></div>
        <div class="col-sm-2 d-flex justify-content-end">開始日</div>
        <div class="col-sm-10 x-valid-from"></div>
        <div class="col-sm-2 d-flex justify-content-end">終了日</div>
        <div class="col-sm-10 x-valid-upto"></div>
    </div>
`;

export class KouhiDisp {
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
        this.futanshaElement = map.futansha;
        this.jukyuushaElement = map.jukyuusha;
        this.validFromElement = map.validFrom;
        this.validUptoElement = map.validUpto;
    }

    set(kouhi){
        if( kouhi ){
            this.futanshaElement.innerText = kouhi.futansha;
            this.jukyuushaElement.innerText = kouhi.jukyuusha;
            this.validFromElement.innerText = dateRep(kouhi.validFrom);
            this.validUptoElement.innerText = dateRep(kouhi.validUpto);
        } else {
            this.futanshaElement.innerText = "";
            this.jukyuushaElement.innerText = "";
            this.validFromElement.innerText = "";
            this.validUptoElement.innerText = "";
        }
        return this;
    }
}

function dateRep(sqldate){
    return kanjidate.sqldateToKanji(sqldate, {
        zeroValue: "（なし）",
        padZero: true
    });
}

