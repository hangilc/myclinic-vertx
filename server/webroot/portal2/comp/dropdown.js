
export function createDropdown(button, linkSpecs) { // linkSpecs: [{label, action}, ...]
    let links = document.createElement("div");
    links.classList.add("dropdown-links");
    let backdrop = document.createElement("div");
    backdrop.classList.add("dropdown-backdrop");
    backdrop.onclick = () => {
        links.style.display = "none";
        backdrop.remove();
    };
    for (let spec of linkSpecs) {
        let key = spec.label;
        let act = spec.action;
        let link = document.createElement("a");
        link.innerText = key;
        link.href = "javascript:void(0)";
        link.onclick = event => {
            links.style.display = "none";
            backdrop.remove();
            act();
        };
        links.append(link);
    }
    button.onclick = event => {
        document.body.append(backdrop);
        let r = button.getBoundingClientRect();
        links.style.left = `${r.left}px`;
        links.style.top = `${r.bottom}px`;
        document.body.append(links);
        links.style.display = "block";
    };
}