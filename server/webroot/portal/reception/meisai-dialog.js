import {ModalDialog2} from "./modal-dialog2.js";
import {parseElement} from "../js/parse-node.js";
import * as kanjidate from "../js/kanjidate.js";
import {createElementFrom} from "../js/create-element-from.js";

let tmpl = `
<div class="x-visit">
    <span class="x-name mr-1"></span>
    (<span class="x-patient-id"></span>)
    <span class="x-datetime"></span>
</div>
<div class="mt-2 x-detail"></div>
<div class="mt-2 x-summary" style="max-width:400px"></div>
`;

let sectionTitleTmpl = `
<div class="x-label lead"></div>
`;

let sectionItemTmpl = `
<div class="x-label ml-4" style="max-width:400px"></div>
`;

let commandsTmpl = `
    <button class="btn btn-primary x-close">閉じる</button>
`;

export class MeisaiDialog extends ModalDialog2 {
    constructor(meisai, patient, visit, charge){
        super("会計明細");
        this.getContent().innerHTML = tmpl;
        let map = parseElement(this.getContent());
        map.name.innerText = `${patient.lastName}${patient.firstName}`;
        map.patientId.innerText = patient.patientId;
        map.datetime.innerText = formatDatetime(visit.visitedAt);
        for(let sect of meisai.sections){
            let title = createSectionTitle(sect.label);
            map.detail.appendChild(title);
            for(let item of sect.items){
                let itemElement = createSectionItem(item.label, item.tanka, item.count);
                map.detail.appendChild(itemElement);
            }
        }
        map.summary.innerText = summaryText(meisai, charge);
        this.getCommands().innerHTML = commandsTmpl;
        let cmap = parseElement(this.getCommands());
        cmap.close.addEventListener("click", event => this.close(true));
    }
}

function summaryText(meisai, charge){
    let chargeAmount = charge ? charge.charge : 0;
    return `総点：${meisai.totalTen.toLocaleString()}点、保険：${meisai.hoken.rep}、負担割合：${meisai.futanWari}割、`
        + `請求額：${chargeAmount.toLocaleString()}円`;
}

function createSectionTitle(label){
    let e = createElementFrom(sectionTitleTmpl);
    let map = parseElement(e);
    map.label.innerText = label;
    return e;
}

function createSectionItem(label, tanka, count){
    let sum = tanka * count;
    let t = `${label} ${tanka}x${count} = ${sum}`;
    let e = createElementFrom(sectionItemTmpl);
    let map = parseElement(e);
    map.label.innerText = t;
    return e;
}

function formatDatetime(sqldatetime){
    return kanjidate.sqldatetimeToKanji(sqldatetime, {
        padZero: true,
        omitSecond: true
    })
}
