import * as F from "../functions.js";

export function createShinryouDisp(name, tekiyou){
    let ele = document.createElement("div");
    ele.innerText = F.createShinryouLabel(name, tekiyou);
    return ele;
}
