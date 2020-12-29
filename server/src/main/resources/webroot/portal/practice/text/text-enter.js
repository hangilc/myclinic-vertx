import {Component} from "../component.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";

let tmpl = `
    <div class="mt-2">
        <textarea class="form-control x-textarea" rows="6"></textarea>
        <div class="form-inline mt-2">
            <a href="javascript:void(0)" class="x-enter">入力</a>
            <a href="javascript:void(0)" class="x-cancel ml-2">キャンセル</a>
        </div>
    </div>
`;

export class TextEnter {
    constructor(prop, visitId){
        this.prop = prop;
        this.visitId = visitId;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.enter.addEventListener("click", async event => this.doEnter());
        this.map.cancel.addEventListener("click", event => this.ele.dispatchEvent(new Event("cancel")));
    }

    async doEnter() {
        let content = this.map.textarea.value;
        let text = {
            visitId: this.visitId,
            content: content
        };
        let textId = await this.prop.rest.enterText(text);
        let entered = await this.prop.rest.getText(textId);
        this.ele.dispatchEvent(new CustomEvent("entered", {detail: entered}));
    }

}

class TextEnterOrig extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.textareaElement = map.textarea;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
    }

    init(visitId) {
        this.visitId = visitId;
        this.enterElement.on("click", event => this.doEnter());
        this.cancelElement.on("click", event => this.ele.trigger("cancel"));
    }

    onEntered(cb){
        this.ele.on("text-entered", (event, text) => cb(event, text));
    }

    async doEnter() {
        let content = this.textareaElement.val();
        let text = {
            visitId: this.visitId,
            content: content
        };
        let textId = await this.rest.enterText(text);
        let entered = await this.rest.getText(textId);
        this.ele.trigger("text-entered", entered);
    }

    onCancel(cb) {
        this.ele.on("cancel", cb);
    }
}