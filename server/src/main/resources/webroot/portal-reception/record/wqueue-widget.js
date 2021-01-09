import {Widget} from "../../js/widget.js";
import {Item} from "./item.js";

export class WqueueWidget extends Widget {
    constructor(patients) {
        super();
        this.patients = patients;
        this.setTitle("受付患者");
        this.updateItems();
    }

    updateItems() {
        const e = this.getBody();
        e.innerHTML = "";
        this.patients.forEach(patient => {
            const item = new Item(patient);
            e.append(item.ele);
        })
    }
}

