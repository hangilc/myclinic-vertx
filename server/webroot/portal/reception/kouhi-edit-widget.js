import {Widget} from "./widget.js";
import {KouhiForm} from "./kouhi-form.js";
import {DateInput} from "./date-input.js";
import {RadioInput} from "./radio-input.js";

export class KouhiEditWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        console.log(map.form);
        let formMap = Object.assign({}, map.form, {
            validFrom: new DateInput(map.form.validFrom),
            validUpto: (new DateInput(map.form.validUpto)).allowEmpty()
        });
        this.form = new KouhiForm(formMap);
        this.closeElement = map.close;
        this.enterElement = map.enter;
        this.clearValidUptoElement = map.form.validUpto.clearValidUpto;
    }

    init(){
        super.init();
        this.form.init();
        this.enterElement.on("click", event => this.doEnter());
        this.closeElement.on("click", event => this.close());
        if( this.clearValidUptoElement ){
            this.clearValidUptoElement.on("click", event => this.form.clearValidUpto());
        }
        return this;
    }

    set(kouhi){
        super.set();
        this.kouhi = kouhi;
        this.form.set(kouhi);
        return this;
    }

    onUpdated(cb){
        this.on("updated", (event, updated) => cb(updated));
    }

    async doEnter(){
        let kouhiId = this.kouhi.kouhiId;
        let patientId = this.kouhi.patientId;
        if( !(kouhiId > 0) ){
            alert("Invalid kouhiId");
            return;
        }
        if( !(patientId > 0) ){
            alert("Invalid patientId");
            return;
        }
        let data = this.form.get(kouhiId, patientId);
        if( !data ){
            let err = this.form.getError();
            alert(err);
            return;
        }
        await this.rest.updateKouhi(data);
        let updated = await this.rest.getKouhi(kouhiId);
        this.trigger("updated", updated);
    }
}