import {Widget} from "./widget.js";
import {DateInput} from "./date-input.js";
import {KouhiForm} from "./kouhi-form.js";

export class KouhiNewWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        let formMap = Object.assign({}, map.form, {
            validFrom: new DateInput(map.form.validFrom),
            validUpto: (new DateInput(map.form.validUpto)).allowEmpty()
        });
        this.form = new KouhiForm(formMap);
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
        let kouhiId = await this.rest.enterKouhi(data);
        let entered = await this.rest.getKouhi(kouhiId);
        this.trigger("entered", entered);
    }

}
