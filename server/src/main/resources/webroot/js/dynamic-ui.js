export function enableUI(ee, cond){
    if( !Array.isArray(ee) ){
        ee = [ee];
    }
    for(let e of ee) {
        e.disabled = !cond;
    }
}

export function showUI(ee, cond){
    if( !Array.isArray(ee) ){
        ee = [ee];
    }
    for(let e of ee){
        if( cond ){
            e.classList.remove("d-none");
        } else {
            e.classList.add("d-none");
        }
    }
}
