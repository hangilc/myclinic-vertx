import * as app from "../app.js";

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

export async function search(textInput, date, searchKind, select){
    const text = textInput.value.trim();
    if( !text ){
        return;
    }
    if( !date ){
        return;
    }
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



