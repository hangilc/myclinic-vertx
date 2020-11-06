import {Component} from "./component.js";
import * as consts from "../js/consts.js";

function getGazouLabel(conductFull){
    if( conductFull.gazouLabel ){
        return conductFull.gazouLabel.label || "";
    } else {
        return "";
    }
}

export class ConductDisp extends Component {
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