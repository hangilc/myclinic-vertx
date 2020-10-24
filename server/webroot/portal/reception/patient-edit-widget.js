import {Widget} from "./widget.js";
import {PatientForm} from "./patient-form.js";

export class PatientEditWidget extends Widget {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.form = new PatientForm(map.form);
        this.closeElement = map.close;
        this.enterElement = map.enter;
    }

    init() {
        super.init();
        this.closeElement.on("click", event => this.close());
        this.enterElement.on("click", event => this.doEnter());
        return this;
    }

    onUpdated(cb) {
        this.on("updated", (event, updated) => cb(updated));
    }

    async doEnter() {
        let data = this.form.get();
        if (data === undefined) {
            alert("エラー：" + this.form.getError());
            return;
        }
        await this.rest.updatePatient(data);
        let updated = await this.rest.getPatient(data.patientId);
        this.trigger("updated", updated);
    }

    set(patient) {
        super.set();
        this.form.set(patient);
        return this;
    }
}