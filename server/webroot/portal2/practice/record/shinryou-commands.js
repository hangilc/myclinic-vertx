import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";

let html = `
<a href="javascript:void(0)" class="x-add-regular">診療行為</a> | 
<a href="javascript:void(0)">その他▼</a>
`;

export function populateShinryouCommands(ele){
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.addRegular.onclick = event => ele.dispatchEvent(F.event("add-regular-shinryou"));
}