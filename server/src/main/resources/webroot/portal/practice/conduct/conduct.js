import {createElementFrom} from "../../../js/create-element-from.js";
import {ConductDisp} from "./conduct-disp.js";

let tmpl = `
    <div></div>
`;

export class Conduct {
    constructor(conductFull){
        this.ele = createElementFrom(tmpl);
        this.disp = new ConductDisp(conductFull);
        this.ele.append(this.disp.ele);
    }
}