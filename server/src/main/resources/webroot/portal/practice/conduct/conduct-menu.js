import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {click} from "../../../js/dom-helper.js";
import {AddXpWidget} from "./add-xp-widget.js";

let tmpl = `
    <div class="dropdown">
        <button type="button" class="btn btn-link pl-0 dropdown-toggle"
                data-toggle="dropdown">［処置］
        </button>
        <div class="dropdown-menu x-shinryou-aux-menu">
            <a href="javascript:void(0)" class="x-add-xp dropdown-item">Ｘ線検査追加</a>
            <a href="javascript:void(0)" class="x-add-injection dropdown-item">注射追加</a>
            <a href="javascript:void(0)" class="x-copy-all dropdown-item">全部コピー</a>
        </div>
    </div>
`;

export class ConductMenu {
    constructor(prop, workarea, wrapper, visitId){
        this.prop = prop;
        this.workarea = workarea;
        this.wrapper = wrapper;
        this.visitId = visitId;
        this.ele = createElementFrom(tmpl);
        let map = parseElement(this.ele);
        click(map.addXp, event => this.doXp());
        click(map.addInjection, event => this.doInjection());
        click(map.copyAll, event => this.doCopyAll());
    }

    doXp(){
        if( !this.prop.confirmManip(this.visitId, "Ｘ線検査を追加しますか") ){
            return;
        }
        let w = new AddXpWidget(this.prop.rest, this.visitId);
        this.workarea.prepend(w.ele);
    }

    doInjection() {

    }

    doCopyAll() {

    }
}