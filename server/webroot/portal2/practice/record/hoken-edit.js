import {parseElement} from "../../js/parse-element.js";

let html = `
<h3>保険選択</h3>
<div class="x-select"></div>
<div class="command-box">
    <button>入力</button>
    <button>キャンセル</button>
</div>
`;

export function createHokenEdit(hoken, visitId, avails, rest){
    let ele = document.createElement("div");
    ele.classList.add("workarea");
    ele.innerHTML = html;
    let map = parseElement(ele);
    for(let shahokokuho of avails.shahokokuhoList){
        shahokokuho.kind = "shahokokuho";
        let ch = createCheck(shahokokuho);
        map.select.append(ch);
    }
    for(let koukikourei of avails.koukikoureiList){
        koukikourei.kind = "koukikourei";
        let ch = createCheck(koukikourei);
        map.select.append(ch);
    }
    for(let roujin of avails.roujinList){
        roujin.kind = "roujin";
        let ch = createCheck(roujin);
        map.select.append(ch);
    }
    for(let kouhi of avails.kouhiList){
        kouhi.kind = "kouhi";
        let ch = createCheck(kouhi);
        map.select.append(ch);
    }
    return ele;
}

function createCheck(hoken){
    let d = document.createElement("div");
    let check = document.createElement("input");
    check.type = "checkbox";
    let label = document.createElement("span");
    label.innerText = hoken.rep;
    d.append(check);
    d.append(label);
    return d;
}