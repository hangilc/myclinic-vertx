import {Widget} from "./widget.js";
import {KoukikoureiForm} from "./koukikourei-form.js";

export class KoukikoureiEditWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.closeElement = map.close;
    }

    init(){
        super.init();
        this.closeElement.on("click", event => this.close());
        return this;
    }

    set(koukikourei){
        super.set();
        return this;
    }

    onUpdated(cb){
        this.on("updated", (event, updated) => cb(updated));
    }
}