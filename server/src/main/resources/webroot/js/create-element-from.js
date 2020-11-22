export function createElementFrom(html){
    let tmpl = document.createElement("template");
    tmpl.innerHTML = html.trim();
    return tmpl.content.firstElementChild;
}