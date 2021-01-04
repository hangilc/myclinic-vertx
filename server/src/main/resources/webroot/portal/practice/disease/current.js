import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import * as DiseaseUtil from "../../js/disease-util.js";
import {click, on} from "../../../js/dom-helper.js";

let tmpl = `
    <div class="disease-ui"></div>
`;

export class Current {
    constructor(props) {
        this.props = props;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.updateUI();
        on(this.ele, "update-ui", event => this.updateUI());
    }

    updateUI(){
        console.log("updateUI", this.props.diseases);
        this.ele.innerHTML = "";
        this.props.diseases.forEach(diseaseFull => {
            let label = new Label(diseaseFull);
            this.ele.append(label.ele);
        })
    }

}

let labelTmpl = `
    <div></div>
`;

class Label {
    constructor(diseaseFull){
        this.ele = createElementFrom(labelTmpl);
        this.ele.innerText = DiseaseUtil.diseaseFullRep(diseaseFull);
        click(this.ele, event => this.ele.dispatchEvent(new CustomEvent("disease-clicked", {
            bubbles: true,
            detail: diseaseFull.disease.diseaseId
        })));
    }
}
