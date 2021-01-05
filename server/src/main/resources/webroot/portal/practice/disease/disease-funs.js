
function createOption(label, data, kind){
    const opt = document.createElement("option");
    opt.innerText = label;
    opt.data = data;
    opt.dataset.kind = kind;
    return opt;
}

export function createByoumeiOption(master){
    return createOption(master.name, master, "byoumei");
}

export function createAdjOption(master){
    return createOption(master.name, master, "adj");
}



