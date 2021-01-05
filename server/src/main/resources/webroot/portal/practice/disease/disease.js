import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {on, click} from "../../../js/dom-helper.js";
import {Current} from "./current.js";
import {Add} from "./add.js";

let tmpl = `
    <div class="disease-listener">
        <h5>病名</h5>
        <div class="x-workarea"></div>
        <div class="x-commands mt-2">
            <a href="javascript:void(0)" class="x-current">現行</a>
            <a href="javascript:void(0)" class="x-add">追加</a>
            <a href="javascript:void(0)" class="x-end">転機</a>
            <a href="javascript:void(0)" class="x-edit">編集</a>
        </div>
    </div>
`;

export class Disease {
    constructor() {
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.props = {diseases: []};
        this.showCurrent();
        click(this.map.add, event => this.showAdd());
        on(this.ele, "disease-loaded", event => {
            this.props.diseases = event.detail;
            this.updateUI();
        });
    }

    showCurrent(){
        let current = new Current(this.props);
        this.map.workarea.innerHTML = "";
        this.map.workarea.append(current.ele);
    }

    showAdd(){
        let add = new Add();
        this.map.workarea.innerHTML = "";
        this.map.workarea.append(add.ele);
        add.initFocus();
    }

    updateUI() {
        let evt = new Event("update-ui");
        this.ele.querySelectorAll(".disease-ui").forEach(e => e.dispatchEvent(evt));
    }
}

