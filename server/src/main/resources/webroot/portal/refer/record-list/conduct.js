import {Component} from "../component.js";
import {parseElement} from "../../js/parse-element.js";
import * as consts from "../../../js/consts.js";

let template = `
    <div>
        <div class="x-kind"></div>
        <div class="x-gazou-label"></div>
        <div class="x-shinryou"></div>
        <div class="x-drug"></div>
        <div class="x-kizai"></div>
    </div>
`;

class Conduct extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.kindElement = map.kind;
        this.gazouLabelElement = map.gazouLabel;
        this.shinryouElement = map.shinryou;
        this.drugElement = map.drug;
        this.kizaiElement = map.kizai;
    }

    init(){
        super.init();
    }

    set(conductFull){
        super.set();
        this.kindElement.text(consts.conductKindToKanji(conductFull.conduct.kind));
        this.gazouLabelElement.text(conductFull.gazouLabel.label || "");
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

class ConductFactory {
    create(conductFull, rest){
        let ele = $(template);
        let map = parseElement(ele);
        let comp = new Conduct(ele, map, rest);
        comp.init();
        comp.set(conductFull);
        return comp;
    }
}

export let conductFactory = new ConductFactory();
