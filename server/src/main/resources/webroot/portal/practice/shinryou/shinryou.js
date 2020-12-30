import {Component} from "../component.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {ShinryouDisp} from "./shinryou-disp.js";

let tmpl = `
    <div class="practice-shinryou" data-shinryoucode="0"></div>
`;

export class Shinryou {
    constructor(shinryouFull){
        this.ele = createElementFrom(tmpl);
        this.ele.dataset.shinryoucode = shinryouFull.master.shinryoucode;
        let disp = new ShinryouDisp(shinryouFull.master.name);
        disp.ele.addEventListener("edit", event => this.doEdit());
        this.ele.append(disp.ele);
    }

    doEdit(){

    }
}

class ShinryouOrig extends Component {
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
            compEdit.onShinryouChanged(shinryouFull => compDisp.init(shinryouFull));
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