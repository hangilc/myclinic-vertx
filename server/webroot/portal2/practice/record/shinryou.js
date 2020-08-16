import {createShinryouDisp} from "./shinryou-disp.js";
import {createShinryouEdit} from "./shinryou-edit.js";

export function createShinryou(shinryouFull, rest){
    let ele = document.createElement("div");
    ele.classList.add("shinryou");
    ele.dataset.shinryoucode = shinryouFull.shinryou.shinryoucode;
    ele.dataset.shinryouId = shinryouFull.shinryou.shinryouId;
    let disp = createShinryouDisp(shinryouFull.master.name);
    disp.onclick = event => {
        let edit = createShinryouEdit(shinryouFull, rest);
        edit.addEventListener("close", event => {
            event.stopPropagation();
            edit.remove();
            ele.append(disp);
        });
        disp.remove();
        ele.append(edit);
    };
    ele.append(disp);
    return ele;
}