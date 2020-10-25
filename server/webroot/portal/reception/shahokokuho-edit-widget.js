import {Widget} from "./widget.js";
import {ShahokokuhoForm} from "./shahokokuho-form.js";
import {DateInput} from "./date-input.js";
import {RadioInput} from "./radio-input.js";

export class ShahokokuhoEditWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        let formMap = Object.assign({}, map.form, {
            honnin: new RadioInput(map.form_, "honnin"),
            validFrom: new DateInput(map.form.validFrom.get(0)),
            validUpto: (new DateInput(map.form.validUpto.get(0))).allowEmpty(),
            kourei: new RadioInput(map.form_, "kourei")
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
        this.enterElement.on("click", event => this.doEnter());
        if( this.clearValidUptoElement ){
            this.clearValidUptoElement.on("click", event => this.form.clearValidUpto());
        }
        return this;
    }

    set(shahokokuho){
        super.set();
        this.shahokokuho = shahokokuho;
        this.form.set(shahokokuho);
        return this;
    }

    onUpdated(cb){
        this.on("updated", (event, updated) => cb(updated));
    }

    async doEnter(){
        let data = this.form.get(this.shahokokuho.shahokokuhoId, this.shahokokuho.patientId);
        if( !data ){
            let err = this.form.getError();
            alert(err);
            return;
        }
        await this.rest.updateShahokokuho(data);
        let updated = await this.rest.getShahokokuho(this.shahokokuho.shahokokuhoId);
        this.trigger("updated", updated);

    }
}