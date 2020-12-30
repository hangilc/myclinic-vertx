import {Component} from "../component.js";
import {click} from "../../../js/dom-helper.js";
import {parseElement} from "../../../js/parse-node.js";
import {createElementFrom} from "../../../js/create-element-from.js";

let tmpl = `
    <div>
        <span class="x-label"></span><span class="x-tekiyou"></span>
    </div>
`;

export class ShinryouDisp {
    constructor(label, tekiyou){
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.label.innerText = label;
        this.updateTekiyou(tekiyou);
        click(this.ele, event => this.ele.dispatchEvent(new Event("edit")));
    }

    updateTekiyou(tekiyou){
        if( tekiyou ){
            this.map.tekiyou.innerText = `［${tekiyou}］`;
        } else {
            this.map.tekiyou.innerText = "";
        }
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