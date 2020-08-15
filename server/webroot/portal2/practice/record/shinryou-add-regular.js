import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";

let html = `
<h3>診療行為入力</h3>
<div class="x-content"></div>
<div class="command-box">
    <button class="x-enter">入力</button>
    <button class="x-cancel">キャンセル</button>
</div>
`;

let itemsTmpl = `
<div class="x-left-items left-items"></div>
<div class="x-right-items right-items"></div>
<div class="x-bottom-items bottom-items"></div>
`;

let leftItems = [
    "初診",
    "再診",
    "外来管理加算",
    "特定疾患管理",
    "-",
    "尿便検査判断料",
    "血液検査判断料",
    "生化Ⅰ判断料",
    "生化Ⅱ判断料",
    "免疫検査判断料",
    "微生物検査判断料",
    "静脈採血",
];

let rightItems = [
    "尿一般",
    "便潜血",
    "-",
    "処方箋料",
    "特定疾患処方管理加算２（処方箋料）",
    "一般名処方加算２（処方箋料）",
    "一般名処方加算１（処方箋料）",
    "-",
    "処方料",
    "処方料７",
    "手帳記載加算",
    "特定疾患処方",
    "長期処方",
    "内服調剤",
    "外用調剤",
    "調基",
    "薬剤情報提供",
];

let bottomItems = [
    "向精神薬",
    "心電図",
    "骨塩定量",
];

function createItemsBox(){
    let e = document.createElement("div");
    e.classList.add("items-container");
    e.innerHTML = itemsTmpl;
    let m = parseElement(e);
    leftItems.forEach(label => {
        m.leftItems.append(createCheck(label));
    });
    rightItems.forEach(label => {
        m.rightItems.append(createCheck(label));
    });
    bottomItems.forEach(label => {
        let c = createCheck(label);
        c.style.display = "inline-block";
        m.bottomItems.append(c);
    });

    return e;
}

function createSep(){
    let e = document.createElement("div");
    e.classList.add("sep-row");
    return e;
}

function createCheck(label){
    if( label === "-" ){
        return createSep();
    }
    let e = document.createElement("div");
    e.classList.add("shinryou-item");
    let chk = document.createElement("input");
    chk.type = "checkbox";
    chk.value = label;
    let spn = document.createElement("span");
    spn.classList.add("checkbox-label");
    spn.innerText = label;
    spn.onclick = event => chk.click();
    e.append(chk, spn);
    return e;
}

function collectChecked(wrapper){
    return Array.from(wrapper.querySelectorAll("input[type=checkbox]:checked"))
        .map(chk => chk.value);
}

export function createShinryouAddRegular(visitId, rest){
    let ele = document.createElement("div");
    ele.classList.add("workarea", "shinryou-add-regular");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.content.append(createItemsBox());
    map.enter.onclick = async event => {
        let names = collectChecked(map.content);
        let entered = await rest.batchEnterShinryouByNames(names, visitId);
        let shinryouFulls = await rest.listShinryouFullByIds(entered.shinryouIds);
        let drugFulls = await rest.listDrugFullByIds(entered.drugIds);
        let conductFulls = await rest.listConductFullByIds(entered.conductIds);
        ele.dispatchEvent(F.event("batch-entered", {shinryouFulls, drugFulls, conductFulls}));
        ele.remove();
    };
    map.cancel.onclick = event => ele.dispatchEvent(F.event("cancel"));
    return ele;
}
