import {Component} from "../component.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {TextDisp} from "./text-disp.js";
import {TextEdit} from "./text-edit.js";

let tmpl = `
    <div class="my-1 record-text"></div>
`;

export class Text {
    constructor(prop, text){
        this.prop = prop;
        this.ele = createElementFrom(tmpl);
        this.setDisp(text);
    }

    setDisp(text){
        let disp = new TextDisp(text);
        this.ele.innerHTML = "";
        disp.ele.addEventListener("start-edit", event => {
            event.stopPropagation();
            let edit = new TextEdit(this.prop, text);
            edit.ele.addEventListener("cancel", event => {
                event.stopPropagation();
                this.ele.innerHTML = "";
                this.ele.append(disp.ele);
            });
            edit.ele.addEventListener("updated", event => {
                event.stopPropagation();
                let newText = event.detail;
                this.setDisp(newText);
            });
            edit.ele.addEventListener("deleted", event => {
                event.stopPropagation();
                this.ele.remove();
            });
            this.ele.innerHTML = "";
            this.ele.append(edit.ele);
            edit.initFocus();
        });
        this.ele.append(disp.ele);
    }
}

class TextOrig extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(text, textDispFactory, textEditFactory, classToken){
        this.text = text;
        this.textDispFactory = textDispFactory;
        this.textEditFactory = textEditFactory;
        let compText = textDispFactory.create(text);
        compText.ele.addClass(classToken);
        compText.ele.data("component", this);
        compText.ele.on("click", event => {
            let editor = this.textEditFactory.create(text);
            editor.onUpdated((event, updated) => {
                this.ele.trigger("text-updated", updated);
            });
            editor.onCancel(event => {
                editor.remove();
                compText.appendTo(this.ele);
            });
            editor.onDeleted(event => this.ele.trigger("text-deleted"));
            editor.onCopied((event, copiedText) => {
                editor.remove();
                compText.appendTo(this.ele);
                this.trigger("copied", copiedText);
            });
            editor.replace(compText.ele);
            editor.initFocus();
        });
        compText.appendTo(this.ele);
    }

    getText(){
        return this.text;
    }

    onUpdated(cb){
        this.ele.on("text-updated", (event, updatedText) => cb(event, updatedText));
    }

    onDeleted(cb){
        this.ele.on("text-deleted", event => cb(event));
    }

    onCopied(cb){
        this.on("copied", cb);
    }

}