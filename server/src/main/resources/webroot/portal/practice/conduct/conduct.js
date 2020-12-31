import {createElementFrom} from "../../../js/create-element-from.js";
import {ConductDisp} from "./conduct-disp.js";
import {ConductEdit} from "./conduct-edit.js";
import {replaceNode} from "../../../js/dom-helper.js";

let tmpl = `
    <div></div>
`;

export class Conduct {
    constructor(prop, conductFull){
        this.prop = prop;
        this.ele = createElementFrom(tmpl);
        this.disp = new ConductDisp(conductFull);
        this.disp.ele.addEventListener("edit", async event => {
            if( !this.prop.confirmManip(conductFull.conduct.visitId, "この処置を編集しますか") ){
                return;
            }
            let edit = new ConductEdit(this.prop.rest, conductFull);
            replaceNode(this.disp.ele, edit.ele);
            let result = await edit.wait();
            if( result === "deleted" ){
                this.ele.remove();
            } else {
                this.ele.innerHTML = "";
                this.ele.append(this.disp.ele);
            }
        });
        this.ele.append(this.disp.ele);
    }
}