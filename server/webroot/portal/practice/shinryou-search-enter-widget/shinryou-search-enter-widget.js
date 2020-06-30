import {WidgetBase} from "../widget-base.js";
import {parseElement} from "../../js/parse-element.js";
import {ShinryouSearch} from "./shinryou-search.js";

let commandsTemplate = `
    <div class="mt-2 d-flex justify-content-end x-ele">
        <button type="button" class="btn btn-success x-enter">入力</button>
        <button type="button" class="btn btn-success x-cancel ml-2">キャンセル</button>
    </div>
`;

class ShinryouSearchEnterWidget extends WidgetBase {
    constructor(rest){
        super(rest);
        this.shinryouSearch = new ShinryouSearch(rest);
        this.commandsMap = parseElement($(commandsTemplate));
    }

    init(visitId, visitedAt){
        super.init("診療行為検索・入力");
        this.visitId = visitId;
        this.shinryouSearch.init(visitedAt);
        this.addToBody(this.shinryouSearch.ele);
        this.addToBody(this.commandsMap.ele);
        this.commandsMap.enter.on("click", event => this.doEnter());
        this.commandsMap.cancel.on("click", event => this.close(null));
        return this;
    }

    set(){
        super.set();
        return this;
    }

    async doEnter(){
        let master = this.shinryouSearch.getSelectedData();
        if( !master ){
            return;
        }
        if( !(this.visitId > 0) ){
            alert("Missing visitId");
            return;
        }

    }
}

class ShinryouSearchEnterWidgetFactory {
    create(visitId, visitedAt, rest){
        let comp = new ShinryouSearchEnterWidget(rest);
        comp.init(visitId, visitedAt);
        comp.set();
        return comp;
    }
}

export let shinryouSearchEnterWidgetFactory = new ShinryouSearchEnterWidgetFactory();

