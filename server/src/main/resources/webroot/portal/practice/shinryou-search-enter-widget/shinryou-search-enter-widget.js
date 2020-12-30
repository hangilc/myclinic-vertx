// import {WidgetBase} from "../widget-base.js";
// import {ShinryouSearch} from "./shinryou-search.js";
import {parseElement} from "../../../js/parse-node.js";
import {Widget} from "../widget2.js";
import {click, submit} from "../../../js/dom-helper.js";

let bodyTmpl = `
    <form class="form-inline x-form" onsubmit="return false;">
        <input type="text" class="form-control x-search-text"/>
        <button type="submit" class="btn btn-secondary ml-2">検索</button>
    </form>
    <select size="10" class="x-select form-control mt-2"></select>
`;

let footerTmpl = `
    <button type="button" class="btn btn-primary x-enter">入力</button>
    <button type="button" class="btn btn-secondary x-close ml-2">閉じる</button>
`;

export class ShinryouSearchEnterWidget extends Widget {
    constructor(rest, visitId, visitedAt) {
        super();
        this.rest = rest;
        this.visitId = visitId;
        this.visitedAt = visitedAt.substring(0, 10);
        this.setTitle("診療行為検索・入力");
        this.getBody().innerHTML = bodyTmpl;
        let bmap = this.bmap = parseElement(this.getBody());
        submit(bmap.form, async event => await this.doSearch());
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.enter, async event => await this.doEnter());
        click(fmap.close, event => this.close());
    }

    initFocus(){
        this.bmap.searchText.focus();
    }

    async doSearch(){
        let text = this.bmap.searchText.value.trim();
        if( text ){
            let result = await this.rest.searchShinryouMaster(text, this.visitedAt);
            this.setSearchResult(result);
        }
    }

    setSearchResult(result){
        let wrapper = this.bmap.select;
        wrapper.innerHTML = "";
        for(let master of result){
            let opt = document.createElement("option");
            opt.innerText = master.name;
            opt.data = master;
            wrapper.append(opt);
        }
    }

    async doEnter(){
        let opt = this.bmap.select.querySelector("option:checked");
        if( opt ){
            let master = opt.data;
            let shinryou = {
                visitId: this.visitId,
                shinryoucode: master.shinryoucode
            };
            let shinryouId = await this.rest.enterShinryou(shinryou);
            let entered = await this.rest.getShinryouFull(shinryouId);
            this.ele.dispatchEvent(new CustomEvent("shinryou-entered", {bubbles: true, detail: entered}));
        }
    }

}

// class ShinryouSearchEnterWidgetOrig extends WidgetBase {
//     constructor(rest){
//         super(rest);
//         this.shinryouSearch = new ShinryouSearch(rest);
//         this.commandsMap = parseElement($(commandsTemplate));
//     }
//
//     init(visitId, visitedAt){
//         super.init("診療行為検索・入力");
//         this.visitId = visitId;
//         this.shinryouSearch.init(visitedAt);
//         this.addToBody(this.shinryouSearch.ele);
//         this.addToBody(this.commandsMap.ele);
//         this.commandsMap.enter.on("click", event => this.doEnter());
//         this.commandsMap.cancel.on("click", event => this.close(null));
//         return this;
//     }
//
//     set(){
//         super.set();
//         return this;
//     }
//
//     onEntered(cb){
//         this.on("entered", (event, shinryouFull) => cb(shinryouFull));
//     }
//
//     async doEnter(){
//         let master = this.shinryouSearch.getSelectedData();
//         if( !master ){
//             return;
//         }
//         if( !(this.visitId > 0) ){
//             alert("Missing visitId");
//             return;
//         }
//         let shinryou = {
//             visitId: this.visitId,
//             shinryoucode: master.shinryoucode
//         };
//         let shinryouId = await this.rest.enterShinryou(shinryou);
//         let entered = await this.rest.getShinryouFull(shinryouId);
//         this.trigger("entered", entered);
//         this.close();
//     }
//
//     focus(){
//         this.shinryouSearch.focus();
//     }
// }
//
// class ShinryouSearchEnterWidgetFactory {
//     create(visitId, visitedAt, rest){
//         let comp = new ShinryouSearchEnterWidget(rest);
//         comp.init(visitId, visitedAt);
//         comp.set();
//         return comp;
//     }
// }

// export let shinryouSearchEnterWidgetFactory = new ShinryouSearchEnterWidgetFactory();

