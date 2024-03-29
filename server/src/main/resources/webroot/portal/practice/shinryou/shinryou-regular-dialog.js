import {Component} from "../component.js";
import {Dialog} from "../../../js/dialog2.js";
import {parseElement} from "../../../js/parse-node.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {gensymId} from "../../../js/gensym-id.js";

let bodyTmpl = `
    <div class="row">
        <div class="col-sm-6 mb-2 x-left"></div>
        <div class="col-sm-6 mb-2 x-right"></div>
        <div class="col-sm-12 form-inline justify-content-center x-bottom"></div>
    </div>
`;

let footerTmpl = `
    <button type="button" class="btn btn-primary x-enter">入力</button>
    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
`;

let leftNames = [
    "初診", "再診", "外来管理加算",
    //  "感染症対策加算（初診）", "感染症対策加算（再診）",
    "特定疾患管理", "",
    "尿便検査判断料", "血液検査判断料", "生化Ⅰ判断料",
    "生化Ⅱ判断料", "免疫検査判断料", "微生物検査判断料", "静脈採血"
];

let rightNames = [
    "尿一般", "便潜血", "",
    "処方箋料", "特定疾患処方管理加算２（処方箋料）", "一般名処方加算２（処方箋料）",
    "一般名処方加算１（処方箋料）", "処方料", "処方料７", "特定疾患処方"
];

let bottomNames = [
    "心電図", "骨塩定量"
];

export class ShinryouRegularDialog extends Dialog {
    constructor() {
        super();
        this.setTitle("診療行為入力");
        this.getBody().append(createElementFrom(bodyTmpl));
        this.bmap = parseElement(this.getBody());
        this.addItems()
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        fmap.enter.addEventListener("click", event => this.doEnter());
        fmap.cancel.addEventListener("click", event => this.close(null));
    }

    addItems(){
        leftNames.forEach(name => {
            let check = createCheck(name);
            this.bmap.left.append(check);
        });
        rightNames.forEach(name => {
            let check = createCheck(name);
            this.bmap.right.append(check);
        });
        bottomNames.forEach(name => {
            let check = createCheck(name);
            this.bmap.bottom.append(check);
        });
        gensymId(this.getBody());
    }

    doEnter(){
        this.close(this.getSelected());
    }

    getSelected(){
        let selected = [];
        let wrapper = this.getBody();
        wrapper.querySelectorAll("input:checked").forEach(e => selected.push(e.value));
        return selected;
    }
}

let checkTmpl = `
    <div class="form-check mr-2">
        <input type="checkbox" class="form-check-input" />
        <label class="form-check-label"></label>
    </div>
`;

let checkIndex = 1;

function createCheck(label, value){
    if( !label ){
        return createBlank();
    }
    let id = `gensym-check-${checkIndex}`
    checkIndex += 1;
    if( !value ){
        value = label;
    }
    let e = createElementFrom(checkTmpl);
    let input = e.querySelector("input");
    input.id = id;
    input.value = value;
    let labelElement = e.querySelector("label");
    labelElement.htmlFor = id;
    labelElement.innerText = label;
    return e;
}

const blankTmpl = `
    <div style="height:1.2em;"></div>
`;

function createBlank(){
    return createElementFrom(blankTmpl);
}
