import {parseElement} from "../../js/parse-element.js";

let html = `
<a href="javascript:void(0)" class="x-enter">文章入力</a> | 
<a href="javascript:void(0)" class="x-shohousen-fax">処方箋FAX</a>
`;

export function populateTextCommands(ele){
    ele.classList.add("record-left-commands");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.enter.onclick = event => {
        ele.dispatchEvent(new Event("do-enter-text", {bubbles: true}));
    };
    map.shohousenFax.onclick = event => {
        ele.dispatchEvent(new Event("do-shohousen-fax", {bubbles: true}));
    };
}
