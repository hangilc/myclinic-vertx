import {Component} from "./component.js";

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

    }

}