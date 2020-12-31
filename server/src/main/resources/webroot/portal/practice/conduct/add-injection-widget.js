import {Widget} from "../widget2.js";
import {parseElement} from "../../../js/parse-node.js";
import {click, submit} from "../../../js/dom-helper.js";
import {gensymId} from "../../../js/gensym-id.js";

let bodyTmpl = `
    <div class="form-row">
        <div class="col-4 text-right">
            <div class="d-flex align-items-center justify-content-end" style="height: 100%">薬剤名称：</div>
        </div>
        <div class="col-8 x-name"></div>
    </div>
    <div class="form-row">
        <div class="col-4">
            <div class="d-flex align-items-center justify-content-end" style="height: 100%">用量：</div>
        </div>
        <div class="col-8 form-inline">
                <input type="text" class="form-control mr-1 x-amount" size="4em"/>   
                <span class="x-unit"></span>  
        </div>
    </div>
    <form onsubmit="return false;" class="x-kind">
        <div class="form-check form-check-inline">
            <input type="radio" name="kind" class="form-check-input" id="gensym-hika" value="0" checked/>
            <label for="gensym-hika" class="form-check-label">皮下・筋肉</label>
        </div>
        <div class="form-check form-check-inline">
            <input type="radio" name="kind" class="form-check-input" id="gensym-joumyaku" value="1"/>
            <label for="gensym-joumyaku" class="form-check-label">静注</label>
        </div>
        <div class="form-check form-check-inline">
            <input type="radio" name="kind" class="form-check-input" id="gensym-sonota" value="2"/>
            <label for="gensym-sonota" class="form-check-label">その他</label>
        </div>
    </form>
    <div class="text-right my-2">
        <button class="btn btn-primary x-enter">入力</button>
        <button class="btn btn-secondary x-cancel">キャンセル</button>
    </div>
    <form class="form-inline mb-2 x-search" onsubmit="return false;">
        <input type="text" class="form-control mr-2 x-search-text"/>
        <button type="submit" class="btn btn-light border">検索</button>
    </form>
    <select class="form-control x-search-result" size="6"></select>
`;

export class AddInjectionWidget extends Widget {
    constructor(rest, visitId, date) {
        super();
        this.rest = rest;
        this.visitId = visitId;
        this.date = date;
        this.master = null;
        this.setTitle("注射の入力");
        this.getBody().innerHTML = bodyTmpl;
        gensymId(this.getBody());
        let map = this.map = parseElement(this.getBody());
        submit(map.search, async event => await this.doSearch());
        click(map.enter, async event => await this.doEnter());
        click(map.cancel, event => this.close());
        map.searchResult.addEventListener("change", event => this.doSelectionChange());
    }

    initFocus(){
        this.map.searchText.focus();
    }

    async doSearch(){
        let text = this.map.searchText.value.trim();
        if( !text ){
            return;
        }
        let masters = await this.rest.searchIyakuhinMaster(text, this.date);
        this.setSearchResult(masters);
    }

    setSearchResult(masters){
        let wrapper = this.map.searchResult;
        wrapper.innerHTML = "";
        let opts = masters.map(master => {
            let opt = document.createElement("option");
            opt.innerText = master.name;
            opt.data = master;
            wrapper.append(opt);
        });
    }

    doSelectionChange(){
        let opt = this.map.searchResult.querySelector("option:checked");
        if( opt ){
            let master = opt.data;
            this.setMaster(master);
        }
    }

    setMaster(master){
        this.master = master;
        this.map.name.innerText = master.name;
        this.map.unit.innerText = master.unit;
    }

    getSelectedKind(){
        return +this.map.kind.querySelector("input:checked").value;
    }

    async doEnter(){
        let kind = this.getSelectedKind();
        if( !this.master ){
            alert("薬剤が選択されていません。");
            return;
        }
        let amount = +this.map.amount.value.trim();
        if( isNaN(amount) || !(amount > 0) ){
            alert("用量の入力が不適切です。");
            return;
        }
        let result = await this.rest.enterInjection(this.visitId, kind, this.master.iyakuhincode, amount);
        this.ele.dispatchEvent(new CustomEvent("batch-entered", {bubbles:true, detail: result}));
        this.ele.remove();
    }
}