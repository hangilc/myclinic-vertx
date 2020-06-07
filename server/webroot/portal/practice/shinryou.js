import {Component} from "./component.js";

export class Shinryou extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.ele.data("component", this);
    }

    init(shinryouFull, shinryouDispFactory){
        this.shinryouFull = shinryouFull;
        let compDisp = shinryouDispFactory.create(shinryouFull);
        compDisp.appendTo(this.ele);
    }

    getShinryoucode(){
        return this.shinryouFull.shinryou.shinryoucode;
    }

}