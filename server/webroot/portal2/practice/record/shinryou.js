import {createShinryouDisp} from "./shinryou-disp.js";

export function createShinryou(shinryouFull){
    let ele = document.createElement("div");
    ele.classList.add("shinryou");
    ele.dataset.shinryoucode = shinryouFull.shinryou.shinryoucode;
    let disp = createShinryouDisp(shinryouFull.master.name);
    ele.append(disp);
    return ele;
}