import {Component} from "../component.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {ShinryouDisp} from "./shinryou-disp.js";
import {getShinryouTekiyou} from "../../js/functions.js";
import {ShinryouEdit} from "./shinryou-edit.js";
import {replaceNode} from "../../../js/dom-helper.js";

let tmpl = `
    <div class="practice-shinryou" data-shinryoucode="0" data-shinryou-id="0"></div>
`;

export class Shinryou {
    constructor(prop, shinryouFull, visitId){
        this.prop = prop;
        this.rest = prop.rest;
        this.visitId = visitId;
        this.shinryouId = shinryouFull.shinryou.shinryouId;
        this.label = shinryouFull.master.name;
        this.tekiyou = getShinryouTekiyou(shinryouFull);
        this.ele = createElementFrom(tmpl);
        this.ele.dataset.shinryoucode = shinryouFull.master.shinryoucode;
        this.ele.dataset.shinryouId = this.shinryouId;
        let disp = new ShinryouDisp(shinryouFull.master.name, getShinryouTekiyou(shinryouFull));
        disp.ele.addEventListener("edit", async event => await this.doEdit(disp));
        this.ele.append(disp.ele);
    }

    doEdit(disp){
        if( !this.prop.confirmManip(this.visitId, "この診療行為を編集していいですか？") ){
            return;
        }
        let edit = new ShinryouEdit(this.rest, this.shinryouId, this.label, this.tekiyou);
        edit.ele.addEventListener("close", event => replaceNode(edit.ele, disp.ele));
        edit.ele.addEventListener("tekiyou-modified", event => {
            let newTekiyou = event.detail;
            disp.updateTekiyou(newTekiyou);
        });
        replaceNode(disp.ele, edit.ele);
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