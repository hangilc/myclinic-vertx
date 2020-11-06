import {createHokenDisp} from "./hoken-disp.js";
import {createHokenEdit} from "./hoken-edit.js";
import * as F from "../functions.js";

export function populateHoken(ele, hoken, visit, rest){
    let disp = createHokenDisp(hoken.rep);
    disp.onclick = async event => {
        event.stopPropagation();
        let avails = await rest.listAvailableAllHoken(visit.patientId, visit.visitedAt.substring(0, 10));
        await extendRep(avails, rest);
        let edit = createHokenEdit(hoken, visit.visitId, avails, rest);
        edit.addEventListener("cancel", event => {
            event.stopPropagation();
            edit.remove();
            ele.append(disp);
        });
        disp.remove();
        ele.append(edit);
    }
    ele.append(disp);
}

async function extendRep(hokenList, rest){
    for(let shaho of hokenList.shahokokuhoList){
        await F.extendShahokokuhoRep(shaho, rest);
    }
    for(let koukikourei of hokenList.koukikoureiList){
        await F.extendKoukikoureiRep(koukikourei, rest);
    }
    for(let roujin of hokenList.roujinList){
        await F.extendRoujinRep(roujin, rest);
    }
    for(let kouhi of hokenList.kouhiList){
        await F.extendKouhiRep(kouhi, rest);
    }
}