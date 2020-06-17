import {Widget} from "./widget.js";
import {RadioInput} from "./radio-input.js";
import {DateInput} from "./date-input.js";
import {KoukikoureiForm} from "./koukikourei-form.js";

export class KoukikoureiNewWidget extends Widget {
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

    init(patientId){
        super.init();
        this.form.init(patientId);
        this.closeElement.on("click", event => this.close());
        this.enterElement.on("click", event => this.doEnter());
        if( this.clearValidUptoElement ){
            this.clearValidUptoElement.on("click", event => this.form.clearValidUpto());
        }
        return this;
    }

    set(){
        super.set();
        return this;
    }

    onEntered(cb){
        this.on("entered", (event, entered) => cb(entered));
    }

    async doEnter(){
        let data = this.form.get();
        if( !data ){
            let err = this.form.getError();
            alert(err);
            return;
        }
        let koukikoureiId = await this.rest.enterKoukikourei(data);
        let entered = await this.rest.getKoukikourei(koukikoureiId);
        this.trigger("entered", entered);
    }

}