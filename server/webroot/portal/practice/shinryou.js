import {Component} from "./component.js";

export class Shinryou extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.ele.data("component", this);
    }

    init(shinryouFull, shinryouDispFactory, shinryouEditFactory){
        this.shinryouFull = shinryouFull;
        let compDisp = shinryouDispFactory.create(shinryouFull);
        compDisp.ele.on("click", event => {
            let compEdit = shinryouEditFactory.create(shinryouFull);
            compEdit.onCancel(event => compDisp.replace(compEdit.ele));
            compEdit.onDeleted(event => this.ele.trigger("deleted"));
            compEdit.replace(compDisp.ele);
        });
        compDisp.appendTo(this.ele);
    }

    getShinryoucode(){
        return this.shinryouFull.shinryou.shinryoucode;
    }

    onDeleted(cb){
        this.ele.on("deleted", cb);
    }

}