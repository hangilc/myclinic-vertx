import {createShinryouDisp} from "./shinryou-disp.js";
import {createShinryouEdit} from "./shinryou-edit.js";
import * as F from "../functions.js";

export function createShinryou(shinryouFull, rest){
    let ele = document.createElement("div");
    ele.classList.add("shinryou");
    ele.dataset.shinryoucode = shinryouFull.shinryou.shinryoucode;
    ele.dataset.shinryouId = shinryouFull.shinryou.shinryouId;
    let disp = doCreateShinryouDisp();
    ele.addEventListener("shinryou-tekiyou-changed", event => {
        F.setShinryouTekiyou(shinryouFull, event.detail.tekiyou);
        disp = doCreateShinryouDisp();
    });
    ele.append(disp);
    return ele;

    function doCreateShinryouDisp(){
        let d = createShinryouDisp(shinryouFull.master.name, F.getShinryouTekiyou(shinryouFull));
        d.onclick = event => {
            let edit = createShinryouEdit(shinryouFull, rest);
            edit.addEventListener("close", event => {
                event.stopPropagation();
                edit.remove();
                ele.append(disp);
            });
            disp.remove();
            ele.append(edit);
        };
        return d;
    }
}