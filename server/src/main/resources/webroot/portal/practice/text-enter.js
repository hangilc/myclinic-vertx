import {Component} from "./component.js";

export class TextEnter extends Component {
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