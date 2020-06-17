import {Widget} from "./widget.js";
import {ShahokokuhoForm} from "./shahokokuho-form.js";
import {DateInput} from "./date-input.js";
import {RadioInput} from "./radio-input.js";

export class ShahokokuhoEditWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        let formMap = Object.assign({}, map.form, {
            validFrom: new DateInput(map.form.validFrom),
            validUpto: (new DateInput(map.form.validUpto)).allowEmpty(),
            futanWari: new RadioInput(map.form_, "futan-wari")
        });
        this.form = new ShahokokuhoForm(formMap);
        this.closeElement = map.close;
        this.enterElement = map.enter;
        this.clearValidUptoElement = map.form.validUpto.clearValidUpto;
    }

    init(){
        super.init();
        this.form.init();
        this.closeElement.on("click", event => this.close());
        return this;
    }

    set(shahokokuho){
        super.set();
        this.form.set(shahokokuho);
        return this;
    }

    onUpdated(cb){
        this.on("updated", (event, updated) => cb(updated));
    }
}