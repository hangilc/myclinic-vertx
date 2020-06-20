import {Widget} from "./widget.js";
import {KoukikoureiForm} from "./koukikourei-form.js";
import {RadioInput} from "./radio-input.js";
import {DateInput} from "./date-input.js";

export class KoukikoureiEditWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        let formMap = Object.assign({}, map.form, {
            validFrom: new DateInput(map.form.validFrom),
            validUpto: (new DateInput(map.form.validUpto)).allowEmpty(),
            futanWari: new RadioInput(map.form_, "futan-wari")
        });
        this.form = new KoukikoureiForm(formMap);
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

    set(koukikourei){
        super.set();
        this.koukikourei = koukikourei;
        this.form.set(koukikourei);
        return this;
    }

    onUpdated(cb){
        this.on("updated", (event, updated) => cb(updated));
    }

    async doEnter(){
        let koukikoureiId = this.koukikourei.koukikoureiId;
        let patientId = this.koukikourei.patientId;
        if( !(koukikoureiId > 0) ){
            alert("Invalid koukikoureiId");
            return;
        }
        if( !(patientId > 0) ){
            alert("Invalid patientId");
            return;
        }
        let data = this.form.get(koukikoureiId, patientId);
        if( !data ){
            let err = this.form.getError();
            alert(err);
            return;
        }
        await this.rest.updateKoukikourei(data);
        let updated = await this.rest.getKoukikourei(koukikoureiId);
        this.trigger("updated", updated);
    }
}