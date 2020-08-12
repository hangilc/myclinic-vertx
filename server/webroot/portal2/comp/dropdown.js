
export function createDropdown(button, linkSpecs) { // linkSpecs: [{label, action}, ...]
    let links = document.createElement("div");
    links.classList.add("dropdown-links");
    let backdrop = document.createElement("div");
    backdrop.classList.add("dropdown-backdrop");
    backdrop.onclick = () => {
        links.style.visibility = "hidden";
        backdrop.remove();
    };
    for (let spec of linkSpecs) {
        let key = spec.label;
        let act = spec.action;
        let link = document.createElement("a");
        link.innerText = key;
        link.href = "javascript:void(0)";
        link.onclick = event => {
            links.style.visibility = "hidden";
            backdrop.remove();
            act();
        };
        links.append(link);
    }
    button.onclick = event => {
        document.body.append(backdrop);
        document.body.append(links);
        let r = getElementRect(button);
        switch(selectPosition()){
            case "down":
            default: {
                if( canPopDown(links, button) ){
                    popDown(links, r);
                } else {
                    popUp(links, r);
                }
                break;
            }
        }
        links.style.visibility = "visible";
    };
}

function selectPosition(){
    return "down";
}

function canPopDown(links, button){
    let rLinks = links.getBoundingClientRect();
    let rButton = button.getBoundingClientRect();
    return rButton.bottom + rLinks.height < window.innerHeight;
}

function popDown(links, r){
    links.style.left = `${r.left}px`;
    links.style.top = `${r.bottom}px`;
}

function popUp(links, r){
    links.style.left = `${r.left}px`;
    let h = links.getBoundingClientRect().height;
    links.style.top = `${r.top - h}px`;
    console.log("body.rect", document.body.getBoundingClientRect());
}

function getElementRect(ele){ // relative to body content (margin excluded)
    let ofs = getBodyOffsets();
    let br = ele.getBoundingClientRect();
    let sx = window.pageXOffset;
    let sy = window.pageYOffset;
    return {
        left: br.left + sx - ofs.left,
        right: br.right + sx - ofs.left,
        top: br.top + sy - ofs.top,
        bottom: br.bottom + sy - ofs.top
    }
}

function getBodyOffsets(){
    let style = window.getComputedStyle(document.body);
    return {
        left: parseInt(style.marginLeft),
        top: parseInt(style.marginTop)
    };
}
