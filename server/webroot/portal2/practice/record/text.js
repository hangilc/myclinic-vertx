import {createTextDisp} from "./text-disp.js";
import {createTextEdit} from "./text-edit.js";

export function createText(text, rest){
    let ele = document.createElement("div");
    ele.classList.add("text");
    let disp = createTextDisp(text.content);
    ele.append(disp);
    ele.addEventListener("do-edit", event => {
        event.stopPropagation();
        let edit = createTextEdit(text, rest);
        disp.remove();
        ele.append(edit);
    });
    ele.addEventListener("do-edit-cancel", event => {
        event.stopPropagation();
        ele.innerHTML = "";
        ele.append(disp);
    });
    ele.addEventListener("text-updated", event => {
        event.stopPropagation();
        text = event.detail;
        disp = createTextDisp(text.content);
        ele.innerHTML = "";
        ele.append(disp);
    })
    return ele;
}
