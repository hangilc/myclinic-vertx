import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";

let html = `
<h3>保険選択</h3>
<div class="x-select"></div>
<div class="command-box">
    <button class="x-enter">入力</button>
    <button class="x-cancel">キャンセル</button>
</div>
`;

export function createHokenEdit(hoken, visitId, avails, rest){
    let ele = document.createElement("div");
    ele.classList.add("workarea");
    ele.innerHTML = html;
    let map = parseElement(ele);
    for(let shahokokuho of avails.shahokokuhoList){
        shahokokuho.kind = "shahokokuho";
        let ch = createCheck(shahokokuho, isCurrentShahokokuho(shahokokuho, hoken));
        map.select.append(ch);
    }
    for(let koukikourei of avails.koukikoureiList){
        koukikourei.kind = "koukikourei";
        let ch = createCheck(koukikourei, isCurrentKoukikourei(koukikourei, hoken));
        map.select.append(ch);
    }
    for(let roujin of avails.roujinList){
        roujin.kind = "roujin";
        let ch = createCheck(roujin, isCurrentRoujin(roujin, hoken));
        map.select.append(ch);
    }
    for(let kouhi of avails.kouhiList){
        kouhi.kind = "kouhi";
        let ch = createCheck(kouhi, isCurrentKouhi(kouhi, hoken));
        map.select.append(ch);
    }
    map.enter.onclick = async event => {
        let visit = getSelctedHoken(map.select, visitId);
        await rest.updateHoken(visit);
        let hoken = await rest.getHoken(visitId);
        ele.dispatchEvent(F.event("hoken-updated", hoken));
    };
    map.cancel.onclick = event => ele.dispatchEvent(F.event("cancel"));
    return ele;
}

function getSelctedHoken(wrapper, visitId){
    let visit = {visitId,
        shahokokuhoId: 0,
        koukikoureiId: 0,
        roujinId: 0,
        kouhi1Id: 0,
        kouhi2Id: 0,
        kouhi3Id: 0,
    };
    let checks = wrapper.querySelectorAll("input[type=checkbox]:checked");
    for(let check of checks){
        let data = check.data;
        let kind = data.kind;
        switch(kind){
            case "shahokokuho": {
                visit.shahokokuhoId = data.shahokokuhoId;
                break;
            }
            case "koukikourei": {
                visit.koukikoureiId = data.koukikoureiId;
                break;
            }
            case "roujin": {
                visit.roujinId = data.roujinId;
                break;
            }
            case "kouhi": {
                if( visit.kouhi1Id === 0 ){
                    visit.kouhi1Id = data.kouhiId;
                    break;
                }
                if( visit.kouhi2Id === 0 ){
                    visit.kouhi2Id = data.kouhiId;
                    break;
                }
                if( visit.kouhi3Id === 0 ){
                    visit.kouhi3Id = data.kouhiId;
                    break;
                }
                console.log("Too many kouhi selected.");
                break;
            }
            default: {
                console.log("Unknown kind: " + kind);
                break;
            }
        }
    }
    return visit;
}

function isCurrentShahokokuho(shahokokuho, hoken){
    return hoken.shahokokuho && hoken.shahokokuho.shahokokuhoId === shahokokuho.shahokokuhoId;
}

function isCurrentKoukikourei(koukikourei, hoken){
    return hoken.koukikourei && hoken.koukikourei.koukikoureiId === koukikourei.koukikoureiId;
}

function isCurrentRoujin(roujin, hoken){
    return hoken.roujin && hoken.roujin.roujinId === roujin.roujinId;
}

function isCurrentKouhi(kouhi, hoken){
    let kouhiId = kouhi.kouhiId;
    if( hoken.kouhi1 && hoken.kouhi1.kouhiId === kouhiId ){
        return true;
    }
    if( hoken.kouhi2 && hoken.kouhi2.kouhiId === kouhiId ){
        return true;
    }
    if( hoken.kouhi3 && hoken.kouhi3.kouhiId === kouhiId ){
        return true;
    }
    return false;
}

function createCheck(hoken, checked){
    let d = document.createElement("div");
    let check = document.createElement("input");
    check.type = "checkbox";
    check.checked = checked;
    let label = document.createElement("span");
    label.innerText = hoken.rep;
    d.append(check);
    d.append(label);
    check.data = hoken;
    return d;
}