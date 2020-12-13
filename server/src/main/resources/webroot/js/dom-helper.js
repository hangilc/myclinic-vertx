
export function show(e, show){
    if( show ){
        e.classList.remove("d-none");
    } else {
        e.classList.add("d-none");
    }
}

export function enable(e, enable){
    e.disabled = !enable;
}

export function on(e, event, handler){
    e.addEventListener(event, handler);
}

export function click(e, handler){
    on(e, "click", handler);
}

export function submit(e, handler){
    on(e, "submit", handler);
}

