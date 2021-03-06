import {Component} from "../component.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {click} from "../../../js/dom-helper.js";

let tmpl = `
    <div class="border border-secondary rounded p-2 my-2">
        <div>名称：<span class="x-label"></span></div>
        <div class="x-tekiyou"></div>
        <div class="mt-2">
            <button class="btn btn-secondary x-delete">削除</button>
            <button class="btn btn-secondary x-close">閉じる</button>
            <button class="btn btn-link x-edit-tekiyou">適用編集</button>
        </div>
    </div>
`;

export class ShinryouEdit {
    constructor(rest, shinryouId, label, tekiyou){
        this.rest = rest;
        this.shinryouId = shinryouId;
        this.tekiyou = tekiyou;
        this.ele = createElementFrom(tmpl);
        let map = this.map = parseElement(this.ele);
        map.label.innerText = label;
        this.updateTekiyouUI();
        click(map.delete, async event => await this.doDelete());
        click(map.close, event => this.ele.dispatchEvent(new Event("close")));
        click(map.editTekiyou, async event => await this.doEditTekiyou());
    }

    updateTekiyouUI(){
        if( this.tekiyou ){
            this.map.tekiyou.innerText = `摘要：${this.tekiyou}`;
        } else {
            this.map.tekiyou.innerText = "";
        }
    }

    async doDelete(){
        if( !confirm("この診療行為を削除していいですか？") ){
            return;
        }
        await this.rest.deleteShinryou(this.shinryouId);
        this.ele.dispatchEvent(new CustomEvent("shinryou-deleted", {bubbles: true, detail: [this.shinryouId]}));
    }

    async doEditTekiyou(){
        let value = prompt("摘要の編集", this.tekiyou);
        if( value !== null ){
            await this.rest.setShinryouTekiyou(this.shinryouId, value);
            this.tekiyou = value;
            this.updateTekiyouUI();
            this.ele.dispatchEvent(new CustomEvent("tekiyou-modified", {detail: value}));
        }
    }

}

class ShinryouEditOrig extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.labelElement = map.label;
        this.tekiyouElement = map.tekiyou;
        this.deleteElement = map.delete;
        this.closeElement = map.close;
        this.editTekiyouElement = map.editTekiyou;
    }

    init(shinryouFull){
        this.shinryouFull = shinryouFull;
        this.shinryouId = shinryouFull.shinryou.shinryouId;
        this.labelElement.text(shinryouFull.master.name);
        this.setTekiyouDisp();
        this.deleteElement.on("click", async event => {
            await this.rest.deleteShinryou(shinryouFull.shinryou.shinryouId);
            this.ele.trigger("deleted");
        });
        this.closeElement.on("click", event => this.ele.trigger("cancel"));
        this.editTekiyouElement.on("click", event => this.doEditTekiyou());
    }

    setTekiyouDisp(){
        let tekiyou = this.getCurrentTekiyou();
        if( tekiyou ){
            let text = `［適用：${tekiyou}］`;
            this.tekiyouElement.text(text);
        }
    }

    onCancel(cb){
        this.ele.on("cancel", cb);
    }

    onDeleted(cb){
        this.ele.on("deleted", cb);
    }

    onShinryouChanged(cb){
        this.ele.on("shinryou-changed", (event, shinryouFull) => cb(shinryouFull));
    }

    async doEditTekiyou(){
        if( this.shinryouId ){
            let result = prompt("適用の入力", this.getCurrentTekiyou());
            if( result === null ){
                return;
            }
            await this.rest.setShinryouTekiyou(this.shinryouId, result);
            this.shinryouFull = await this.rest.getShinryouFull(this.shinryouFull.shinryou.shinryouId);
            this.setTekiyouDisp();
            this.ele.trigger("shinryou-changed", this.shinryouFull);
        }
    }

    getCurrentTekiyou(){
        let sf = this.shinryouFull;
        if( sf && sf.attr ){
            return sf.attr.tekiyou;
        } else {
            return null;
        }
    }

}