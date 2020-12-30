import {Component} from "../component.js";
import {click} from "../../../js/dom-helper.js";

export class ShinryouDisp {
    constructor(label){
        this.ele = document.createElement("div");
        this.ele.innerText = label;
        click(this.ele, event => this.ele.dispatchEvent(new Event("edit")));
    }
}



class ShinryouDispOrig extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(shinryouFull){
        this.shinryouFull = shinryouFull;
        this.ele.text(this.composeDisp());
    }

    composeDisp(){
        let text = this.shinryouFull.master.name;
        let tekiyou = this.getTekiyou();
        if( tekiyou ){
            text += `［適用：${tekiyou}］`;
        }
        return text;
    }

    getTekiyou(){
        let sf = this.shinryouFull;
        if( sf && sf.attr ){
            return sf.attr.tekiyou;
        } else {
            return null;
        }
    }

}