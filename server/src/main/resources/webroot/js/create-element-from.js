import {parseElement} from "../js/parse-node.js";

export function createElementFrom(html){
    let tmpl = document.createElement("template");
    tmpl.innerHTML = html.trim();
    return tmpl.content.firstElementChild;
}

export function appendElementsFromTemplate(targetElement, template, needMap=false){
    let tmpl = document.createElement("template");
    tmpl.innerHTML = template;
    let map = null;
    if( needMap ){
        map = parseElement(tmpl.content);
    }
    const children = tmpl.content.children;
    const childrenStore = [];
    for(let i=0;i<children.length;i++){
        const child = children.item(i);
        childrenStore.push(child);
    }
    for(const child of childrenStore){
        targetElement.appendChild(child);
    }
    return map;
}
