import {Component} from "../component.js";
import * as consts from "../../js/consts.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";

let tmpl = `
    <div>
        <div class="x-kind"></div>
        <div class="x-gazou-label"></div>
        <div class="x-shinryou"></div>
        <div class="x-drug"></div>
        <div class="x-kizai"></div>
    </div>
`;

export class ConductDisp {
    constructor(conductFull){
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.kind.innerText = consts.conductKindToKanji(conductFull.conduct.kind);
        this.map.gazouLabel.innerText = getGazouLabel(conductFull);
        conductFull.conductShinryouList.forEach(cs => {
            let e = document.createElement("div");
            e.innerText = conductShinryouRep(cs);
            this.map.shinryou.append(e);
        });
        conductFull.conductDrugs.forEach(cd => {
            let e = document.createElement("div");
            e.innerText = conductDrugRep(cd);
            this.map.drug.append(e);
        });
        conductFull.conductKizaiList.forEach(ck => {
            let e = document.createElement("div");
            e.innerText = conductKizaiRep(ck);
            this.map.kizai.append(e);
        });
    }
}

function getGazouLabel(conductFull){
    if( conductFull.gazouLabel ){
        return conductFull.gazouLabel.label || "";
    } else {
        return "";
    }
}

function conductShinryouRep(conductShinryouFull){
    return conductShinryouFull.master.name;
}

function conductDrugRep(conductDrugFull){
    let name = conductDrugFull.master.name;
    let amount = conductDrugFull.conductDrug.amount;
    let unit = conductDrugFull.master.unit;
    return `${name} ${amount}${unit}`;
}

function conductKizaiRep(conductKizaiFull){
    let name = conductKizaiFull.master.name;
    let amount = conductKizaiFull.conductKizai.amount;
    let unit = conductKizaiFull.master.unit;
    return `${name} ${amount}${unit}`;
}


class ConductDispOrig extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.kindElement = map.kind;
        this.gazouLabelElement = map.gazouLabel;
        this.shinryouElement = map.shinryou;
        this.drugElement = map.drug;
        this.kizaiElement = map.kizai;
    }

    init(conductFull){
        this.kindElement.text(consts.conductKindToKanji(conductFull.conduct.kind));
        this.gazouLabelElement.text(getGazouLabel(conductFull));
        conductFull.conductShinryouList.forEach(cs => {
            let e = $("<div>").text(this.conductShinryouRep(cs));
            this.shinryouElement.append(e);
        });
        conductFull.conductDrugs.forEach(cd => {
            let e = $("<div>").text(this.conductDrugRep(cd));
            this.drugElement.append(e);
        });
        conductFull.conductKizaiList.forEach(ck => {
            let e = $("<div>").text(this.conductKizaiRep(ck));
            this.drugElement.append(e);
        });
    }

    conductShinryouRep(conductShinryouFull){
        return conductShinryouFull.master.name;
    }

    conductDrugRep(conductDrugFull){
        let name = conductDrugFull.master.name;
        let amount = conductDrugFull.conductDrug.amount;
        let unit = conductDrugFull.master.unit;
        return `${name} ${amount}${unit}`;
    }

    conductKizaiRep(conductKizaiFull){
        let name = conductKizaiFull.master.name;
        let amount = conductKizaiFull.conductKizai.amount;
        let unit = conductKizaiFull.master.unit;
        return `${name} ${amount}${unit}`;
    }

}