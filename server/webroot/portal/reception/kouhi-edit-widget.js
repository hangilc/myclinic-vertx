import {Widget} from "./widget.js";
import {KouhiForm} from "./kouhi-form.js";

export class KouhiEditWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.closeElement = map.close;
    }

    init(){
        super.init();
        this.closeElement.on("click", event => this.close());
        return this;
    }

    set(kouhi){
        super.set();
        return this;
    }

    onUpdated(cb){
        this.on("updated", (event, updated) => cb(updated));
    }
}