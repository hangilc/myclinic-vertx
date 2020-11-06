import {Component} from "./component.js";

export class CurrentSetting extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.nameElement = map.name;
        this.modifySettingElement = map.modifySetting;
        this.printReferenceFrameElement = map.printReferenceFrame;
        this.textareaElement = map.textarea;
        this.saveJsonSettingElement = map.saveJsonSetting;
        this.endElement = map.end;
    }

    init(){
        super.init();
        this.modifySettingElement.on("click", event => this.doModifySetting());
        this.saveJsonSettingElement.on("click", event => this.doSaveJsonSetting());
        this.printReferenceFrameElement.on("click", event => this.doPrintReferenceFrame());
        this.endElement.on("click", event => this.doEnd());
        return this;
    }

    set(name, jsonSetting){
        super.set();
        this.name = name;
        if( name ){
            this.nameElement.text(name);
            this.textareaElement.val(JSON.stringify(jsonSetting, null, 2));
            this.ele.removeClass("d-none");
        } else {
            this.nameElement.text("");
            this.textareaElement.val("");
            this.ele.addClass("d-none");
        }
        return this;
    }

    getName(){
        return this.name;
    }

    onJsonSettingUpdated(cb){
        this.on("json-setting-updated", event => cb());
    }

    async doSaveJsonSetting(){
        let name = this.getName();
        if( name ){
            let value = JSON.parse(this.textareaElement.val());
            await this.rest.savePrinterJsonSetting(name, value);
            this.trigger("json-setting-updated");
        }
    }

    async doModifySetting(){
        let name = this.getName();
        if( name ){
            await this.rest.modifyPrinterSetting(name);
        }
    }

    onEnd(cb){
        this.on("end", event => cb());
    }

    doEnd(){
        this.trigger("end");
    }

    async doPrintReferenceFrame(){
        let paper = prompt("用紙サイズ", "A4");
        if( paper ){
            await this.rest.printGuideFrame(paper, this.getName());
        }
    }
}
