import {Dialog} from "../../../js/dialog.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {gensymId} from "../../../js/gensym-id.js";

let tmpl = `
<form class="row x-items-wrapper" onsubmit="return false;">
    <div class="col-6 x-left"></div>
    <div class="col-6 x-right"></div>
</form>
`;

let itemTmpl = `
    <div class="form-check">
        <input type="checkbox" class="form-check-input"/>
        <label class="form-check-label"></label>
    </div>
`;

function createItem(value, label=null){
    if( label == null ){
        label = value;
    }
    let e = createElementFrom(itemTmpl);
    let input = e.querySelector("input");
    let id = `gensym-${value}`;
    input.value = value;
    input.id = id;
    let labelElement = e.querySelector("label");
    labelElement.setAttribute("for", id);
    labelElement.innerText = label;
    return e;
}

let leftItems = [
    "血算",
    "末梢血液像",
    "ＨｂＡ１ｃ",
    "ＰＴ",
    "--sep--",
    "ＧＯＴ",
    "ＧＰＴ",
    "γＧＴＰ",
    "ＣＰＫ",
    "クレアチニン",
    "尿酸",
    "カリウム",
    ["ＬＤＬ－Ｃｈ", "ＬＤＬ－コレステロール"],
    ["ＨＤＬ－Ｃｈ", "ＨＤＬ－コレステロール"],
    "ＴＧ",
    "グルコース",
];

let rightItems = [
    "ＣＲＰ",
    "--sep--",
    "ＴＳＨ",
    "ＦＴ４",
    "ＦＴ３",
    "ＰＳＡ",
    "--sep--",
    "蛋白定量（尿）",
    "クレアチニン（尿）",
];

let presetItems = [
    "血算",
    "ＨｂＡ１ｃ",
    "ＧＯＴ",
    "ＧＰＴ",
    "γＧＴＰ",
    "クレアチニン",
    "尿酸",
    "ＬＤＬ－コレステロール",
    "ＨＤＬ－コレステロール",
    "ＴＧ",
];

function populateItems(e, itemValues){
    itemValues.forEach(value => {
        if( value === "--sep--" ){
            e.append(document.createElement("hr"));
        } else {
            if( Array.isArray(value) ){
                let [l, v] = value;
                e.append(createItem(v, l));
            } else {
                e.append(createItem(value))
            }
        }
    })
}

let commandsTmpl = `
    <button class="btn btn-secondary x-preset">セット検査</button>
    <button class="btn btn-primary x-enter">入力</button>
    <button class="btn btn-secondary x-clear">クリア</button>
    <button class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class ShinryouKensaDialog extends Dialog {
    constructor(visitId, rest){
        super();
        this.visitId = visitId;
        this.rest = rest;
        this.getBody().innerHTML = tmpl;
        let map = this.map = parseElement(this.getBody());
        populateItems(map.left, leftItems);
        populateItems(map.right, rightItems);
        gensymId(this.getBody());
        this.getFooter().innerHTML = commandsTmpl;
        let cmap = parseElement(this.getFooter());
        cmap.preset.addEventListener("click", event => this.doPreset());
        cmap.enter.addEventListener("click", async event => await this.doEnter());
        cmap.clear.addEventListener("click", event => this.doClear());
        cmap.cancel.addEventListener("click", event => this.close(null));
    }

    async doEnter(){
        let names = Array.from(this.map.itemsWrapper.querySelectorAll("input[type='checkbox']:checked"))
            .map(e => e.value);
        let result = await this.rest.batchEnterShinryouByNames(names, this.visitId);
        this.close(result);
    }

    doPreset(){
        presetItems.forEach(pre => {
            this.map.itemsWrapper.querySelector(`input[value='${pre}']`).checked = true;
        });
    }

    doClear(){
        this.map.itemsWrapper.querySelectorAll("input").forEach(e => e.checked = false);
    }
}