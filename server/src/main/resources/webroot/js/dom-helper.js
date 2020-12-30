
export function show(e, show=true){
    if( show ){
        e.classList.remove("d-none");
    } else {
        e.classList.add("d-none");
    }
}

export function hide(e){
    show(e, false);
}

export function toggle(e){
    if( e.classList.contains("d-none") ){
        show(e);
    } else {
        hide(e);
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

export function replaceNode(oldNode, newNode){
    oldNode.parentNode.replaceChild(newNode, oldNode);
}
