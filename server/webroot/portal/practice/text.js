import {Component} from "./component.js";

export class Text extends Component {
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