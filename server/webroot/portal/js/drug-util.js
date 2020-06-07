import * as consts from "./consts.js";
import {toZenkaku} from "./jp-util.js";

export function drugRep(drugFull){
    let drug = drugFull.drug;
    let master = drugFull.master;
    switch(drug.category){
        case consts.DrugCategoryNaifuku: {
            let amount = toZenkaku(drug.amount);
            let days = toZenkaku(drug.days);
            return `${master.name}${amount}${master.unit} ${drug.usage} ${days}日分`;
        }
        case consts.DrugCategoryTonpuku: {
            let amount = toZenkaku(drug.amount);
            let days = toZenkaku(drug.days);
            return `${master.name}${amount}${master.unit} ${drug.usage} ${days}回分`;
        }
        case consts.DrugCategoryGaiyou:
        default:{
            let amount = toZenkaku(drug.amount);
            return `${master.name}${amount}${master.unit} ${drug.usage}`;
        }
    }
}