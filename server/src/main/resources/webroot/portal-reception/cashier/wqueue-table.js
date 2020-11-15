import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {sexToRep, wqueueStateCodeToRep, WqueueStateWaitCashier} from "../js/consts.js";
import * as kanjidate from "../js/kanjidate.js";

let tmpl = `
<table class="table" id="cashier-wqueue-table">
    <thead>
        <tr>
            <th>状態</th>
            <th>#</th>
            <th>氏名</th>
            <th>よみ</th>
            <th>性別</th>
            <th>生年月日</th>
            <th>年齢</th>
            <th>操作</th>
        </tr>
    </thead>
    <tbody class="x-body"></tbody>
</table>
`;

let rowTmpl = `
<tr>
    <td class="x-state align-middle"></td>
    <td class="x-patient-id align-middle"></td>
    <td class="x-name align-middle"></td>
    <td class="x-yomi align-middle"></td>
    <td class="x-sex align-middle"></td>
    <td class="x-birthday align-middle"></td>
    <td class="x-age align-middle"></td>
    <td class="x-manip align-middle"></td>
</tr>
`;

export class WqueueTable {
    constructor(){
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
    }

    clear(){
        this.map.body.innerHTML = "";
    }

    addRow(wq){
        let row = createElementFrom(rowTmpl);
        let map = parseElement(row);
        let data = wqueueToRowData(wq);
        map.state.innerText = data.state;
        map.patientId.innerText = data.patientId;
        map.name.innerText = data.name;
        map.yomi.innerText = data.yomi;
        map.sex.innerText = data.sex;
        map.birthday.innerText = data.birthday;
        map.age.innerText = data.age;
        populateManip(map.manip, wq);
        this.map.body.appendChild(row);
    }

}

function wqueueToRowData(wq){
    let name = wq.patient.lastName + wq.patient.firstName;
    let yomi = wq.patient.lastNameYomi + wq.patient.firstNameYomi
    let birthday = kanjidate.sqldateToKanji(wq.patient.birthday);
    let age = kanjidate.calcAge(wq.patient.birthday);
    return {
        state: wqueueStateCodeToRep(wq.wqueue.waitState),
        patientId: wq.patient.patientId,
        name: name,
        yomi: yomi,
        sex: sexToRep(wq.patient.sex),
        birthday: birthday,
        age: age + "才",
    }
}

let manipTmpl = `
<button class="btn btn-primary x-cashier">会計</button>
<div class="dropdown d-inline">
    <a href="javascript:void(0)" class="btn btn-link dropdown-toggle" data-toggle="dropdown">操作</a>
    <div class="dropdown-menu">
        <a class="dropdown-item x-remove" href="javascript:void(0)">削除</a>
    </div>
</div>
`;

function populateManip(wrapper, wq){
    wrapper.innerHTML = manipTmpl;
    let map = parseElement(wrapper);
    let visitId = wq.wqueue.visitId;
    if( wq.wqueue.waitState !== WqueueStateWaitCashier ){
        map.cashier.classList.add("d-none");
    } else {
        map.cashier.addEventListener("click", event => wrapper.dispatchEvent(
            new CustomEvent("wq-cashier", {bubbles: true, detail: visitId})));
    }
    map.remove.addEventListener("click", event => wrapper.dispatchEvent(
        new CustomEvent("wq-delete", {bubbles: true, detail: visitId})));
    // if( wq.wqueue.waitState === WqueueStateWaitCashier ){
    //     let cashierButton = $("<button>", {class: "btn btn-primary"}).text("会計");
    //     cashierButton.on("click", async event => {
    //         let meisai = await rest.getMeisai(wq.visit.visitId);
    //         let charge = await rest.getCharge(wq.visit.visitId);
    //         let dialog = chargeDialog;
    //         dialog.title = `会計：（${wq.patient.patientId}）${name}（${yomi}）`;
    //         dialog.detail = meisai.sections.map(sect => {
    //             return `${sect.label}：${sect.sectionTotalTen.toLocaleString()} 点`;
    //         }).join("\n");
    //         dialog.summary = `総点：${meisai.totalTen.toLocaleString()} 点、負担割：${meisai.futanWari}割`;
    //         //dialog.value = `請求額：${meisai.charge.toLocaleString()} 円`;
    //         dialog.value = `請求額：${charge.charge.toLocaleString()} 円`;
    //         dialog.setOnEnter(async () => {
    //             await rest.finishCharge(wq.visit.visitId, charge.charge, moment());
    //             dialog.hide();
    //             update();
    //         });
    //         dialog.show();
    //     });
    //     commands = cashierButton;
    // }
}