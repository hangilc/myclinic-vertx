export function createElementFrom(html){
    let tmpl = document.createElement("tmpl");
    tmpl.innerHTML = html.trim();
    return tmpl.firstChild;
}