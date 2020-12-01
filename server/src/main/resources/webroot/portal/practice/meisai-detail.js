import {Component} from "./component.js";

export class MeisaiDetail extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.itemsElement = map.items;
        this.summaryElement = map.summary;
        this.valueElement = map.value;
    }

    init(meisai){
        this.itemsElement.text(meisai.sections.map(sect => {
            return `${sect.label}：${sect.sectionTotalTen.toLocaleString()} 点`;
        }).join("\n"));
        this.summaryElement.text(`総点：${meisai.totalTen.toLocaleString()} 点、負担割：${meisai.futanWari}割`);
        this.updateChargeValue(meisai.charge);
    }

    updateChargeValue(chargeValue){
        this.valueElement.text(`請求額：${chargeValue.toLocaleString()} 円`);
    }
}