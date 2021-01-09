import {Component} from "../component2.js";
import {toZenkaku} from "../../../js/jp-util.js";
import * as consts from "../../../js/consts.js";

let template = `
    <textarea class="my-2 p-1 border rounded d-none form-control" readonly rows="8"></textarea>
`;

export class Disp extends Component {

    constructor(){
        super($(template));
        this.index = 1;
    }

    add(exampleFull){
        let drug = exampleFull.prescExample;
        let master = exampleFull.master;
        let s = getLines(drug, master, this.index++);
        let ta = this.ele;
        let cur = ta.val();
        cur += s;
        ta.val(cur);
    }

    show(){
        this.ele.removeClass("d-none");
    }

}

function getLines(drug, master, index){
    let cat = drug.category;
    switch(drug.category){
        case consts.DrugCategoryNaifuku: return naifukuLines(drug, master, index);
        case consts.DrugCategoryTonpuku: return tonpukuLines(drug, master, index);
        default: return gaiyouLines(drug, master, index);
    }
}

function normalizeName(name){
    name = name.replace(/「.+?」/g, "");
    return name;
}

function naifukuLines(drug, master, index){
    index = toZenkaku("" + index) + "）";
    let name = normalizeName(master.name);
    let pad = "　".repeat(21 - index.length - name.length);
    let amount = toZenkaku("" + drug.amount) + master.unit;
    let line1 = `${index}${name}${pad}${amount}`;
    let line2 = "　".repeat(index.length) + drug.usage + "　" +
        toZenkaku("" + drug.days) + "日分";
    return [line1, line2].map(s => s + "\n").join("");
}

function tonpukuLines(drug, master, index){
    index = toZenkaku("" + index) + "）";
    let name = normalizeName(master.name);
    let line1 = `${index}${name}`;
    let amount = toZenkaku("" + drug.amount) + master.unit;
    let days = toZenkaku("" + drug.days);
    let line2 = "　".repeat(index.length) +
        `１回${amount}　${drug.usage} ${days}回分`
    return [line1, line2].map(s => s + "\n").join("");
}

function gaiyouLines(drug, master, index){
    index = toZenkaku("" + index) + "）";
    let name = normalizeName(master.name);
    let amount = toZenkaku("" + drug.amount) + master.unit;
    let line1 = `${index}${name}　${amount}`;
    let line2 = "　".repeat(index.length) + drug.usage;
    return [line1, line2].map(s => s + "\n").join("");
}

