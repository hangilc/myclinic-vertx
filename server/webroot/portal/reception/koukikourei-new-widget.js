import {Widget} from "./widget.js";
import {RadioInput} from "./radio-input.js";
import {DateInput} from "./date-input.js";
import {KoukikoureiForm} from "./koukikourei-form.js";

export class KoukikoureiNewWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        let formMap = Object.assign({}, map.form, {
            validFrom: new DateInput(map.form.validFrom.get(0)),
            validUpto: (new DateInput(map.form.validUpto.get(0))).allowEmpty(),
            futanWari: new RadioInput(map.form_, "futan-wari")
        });
        this.form = new KoukikoureiForm(formMap);
        this.closeElement = map.close;
        this.enterElement = map.enter;
        this.clearValidUptoElement = map.form.validUpto.clearValidUpto;
    }

    init(patientId){
        super.init();
        this.patientId = patientId;
        this.form.init();
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
        let patientId = this.patientId;
        if( !(patientId > 0) ){
            alert("患者が設定されていません。");
            return null;
        }
        let data = this.form.get(0, patientId);
        if( !data ){
            let err = this.form.getError();
            if( err ){
                alert(err);
            }
            return;
        }
        let koukikoureiId = await this.rest.enterKoukikourei(data);
        let entered = await this.rest.getKoukikourei(koukikoureiId);
        this.trigger("entered", entered);
    }

}