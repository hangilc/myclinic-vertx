import {parseElement} from "../js/parse-node.js";

let tmpl = `
<table class="table mt-3 payment-table" id="reception-wqueue-table" style="height:100px">
    <thead>
    <tr>
        <th scope="col">患者番号</th>
        <th scope="col">氏名</th>
        <th scope="col">金額</th>
        <th scope="col">日時</th>
    </tr>
    </thead>
    <tbody class="x-body"></tbody>
</table>
`;

let itemTmpl = `
    <td class="x-patient-id"></td>
    <td class="x-name"></td>
    <td class="x-amount"></td>
    <td class="x-datetime"></td>
`;

let symData = Symbol("data");

export class PaymentTable {
    constructor(ele){
        if( !ele ){
            ele = document.createElement("div");
        }
        ele.innerHTML = tmpl;
        this.ele = ele;
        this.map = parseElement(this.ele);
    }

    clearItems(){
        this.map.body.innerHTML = "";
    }

    selectItem(tr){
        this.map.body.querySelectorAll("tr").forEach(r => {
            if( r !== tr ) {
                r.classList.remove("table-active");
            }
        });
        tr.classList.add("table-active");
    }

    getSelectedData(){
        let tr = this.map.body.querySelector("tr.table-active");
        return tr ? tr[symData] : null;
    }

    addItem(patientId, name, amount, at, visitId){
        let tr = document.createElement("tr");
        tr.innerHTML = itemTmpl;
        tr[symData] = visitId;
        tr.addEventListener("click", event => this.selectItem(tr));
        let map = parseElement(tr);
        map.patientId.innerText = patientId;
        map.name.innerText = name;
        map.amount.innerText = amount;
        map.datetime.innerText = at;
        this.map.body.appendChild(tr);
    }
}