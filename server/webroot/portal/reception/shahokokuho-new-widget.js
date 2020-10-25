import {Widget} from "./widget.js";
import {DateInput} from "./date-input.js";
import {ShahokokuhoForm} from "./shahokokuho-form.js";
import {RadioInput} from "./radio-input.js";

export class ShahokokuhoNewWidget extends Widget {
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
        if( !(this.patientId > 0) ){
            alert("患者が指定されていません。");
            return;
        }
        let data = this.form.get(0, this.patientId);
        if( !data ){
            let err = this.form.getError();
            alert(err);
            return;
        }
        let shahokokuhoId = await this.rest.enterShahokokuho(data);
        let entered = await this.rest.getShahokokuho(shahokokuhoId);
        this.trigger("entered", entered);
    }
}