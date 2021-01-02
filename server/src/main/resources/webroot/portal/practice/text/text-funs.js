import {shohousenTextContentDataToDisp} from "../funs.js";

export function textRep(content){
    content = shohousenTextContentDataToDisp(content);
    return content.replace(/\r\n|\n|\r/g, "<br/>\n");

}