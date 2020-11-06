import {Dialog} from "./dialog.js";

export class SelectSettingDialog extends Dialog {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.selectElement = map.select;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
    }

    init(){
        super.init();
        this.cancelElement.on("click", event => this.close(null));
        this.enterElement.on("click", event => this.doEnter());
        return this;
    }

    set(settingList){
        super.set();
        for(let setting of settingList){
            let opt = $("<option>");
            opt.text(setting);
            this.selectElement.append(opt);
        }
        return this;
    }

    doEnter(){
        let sel = this.selectElement.val();
        this.close(sel);
    }
}
