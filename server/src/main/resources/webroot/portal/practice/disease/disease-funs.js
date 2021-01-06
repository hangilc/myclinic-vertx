import * as app from "../app.js";
import {failure, success} from "../../../js/result.js";

function createOption(label, data, kind){
    const opt = document.createElement("option");
    opt.innerText = label;
    opt.data = data;
    opt.dataset.kind = kind;
    return opt;
}

export function createExampleOption(example){
    return createOption(exampleRep(example), example, "example");
}

export function exampleRep(example){
    return example.label || example.byoumei;
}

export async function search(text, date, searchKind, select){
    select.innerHTML = "";
    let masters = [];
    if( searchKind === "byoumei" ){
        masters = await app.rest.searchByoumeiMaster(text, date);
    } else if( searchKind === "adj" ){
        masters = await app.rest.searchShuushokugoMaster(text, date);
    }
    masters.forEach(master => select.append(createOption(master.name, master, searchKind)));
}

export function setExamples(examples, select){
    select.innerHTML = "";
    examples.forEach(example => {
        const opt = createExampleOption(example);
        select.append(opt);
    })
}

export function handleSelected(select, byoumeiHandler, adjHandler, exampleHandler){
    let opt = select.querySelector("option:checked");
    if( opt ){
        if( opt.dataset.kind === "byoumei" ){
            byoumeiHandler(opt.data);
        } else if( opt.dataset.kind === "adj" ){
            adjHandler(opt.data);
        } else if( opt.dataset.kind === "example" ){
            exampleHandler(opt.data);
        }
    }
}

export async function resolveExample(example, date, resultHandler){
    const props = {master: null, adjList: []};
    const byoumei = example.byoumei;
    if( byoumei ){
        const master = await app.rest.findByoumeiMasterByName(byoumei, date);
        if( !master ){
            return resultHandler(failure(`傷病名（${byoumei}）を見つけられませんでした。`));
        }
        props.master = master;
    }
    if( example.adjList ) {
        for (const adj of example.adjList) {
            const master = await app.rest.findShuushokugoMasterByName(adj);
            if (!master) {
                return resultHandler(failure(`修飾語（${adj}）を見つけられませんでした。`));
            }
            props.adjList.push(master);
        }
    }
    return resultHandler(success(props));
}




