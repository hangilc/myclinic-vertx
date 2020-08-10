import {parseElement} from "../../js/parse-element.js";

let html = `

`;

export function createText(text, rest){
    let ele = document.createElement("div");
    ele.innerText = text.content;
    return ele;
}
