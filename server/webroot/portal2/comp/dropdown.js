
export function createDropdown(label, linkMap) {
    return (function(){
        let ele = document.createElement("div");
        ele.classList.add("dropdown");
        let a = document.createElement("a");
        a.innerText = label;
        a.href = "javascript:void(0)";
        a.classList.add("dropdown-button");
        ele.append(a);
        let links = document.createElement("div");
        links.classList.add("dropdown-links");
        ele.append(links);
        let backdrop = document.createElement("div");
        backdrop.classList.add("dropdown-backdrop");
        backdrop.onclick = () => {
            links.style.display = "none";
            backdrop.remove();
        };
        for (let key in linkMap) {
            if( !linkMap.hasOwnProperty(key) ){
                continue;
            }
            let act = linkMap[key];
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
        a.onclick = event => {
            document.body.append(backdrop);
            links.style.display = "block";
        };
        return ele;
    })();

}