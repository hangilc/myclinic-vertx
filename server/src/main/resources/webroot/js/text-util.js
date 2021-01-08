export function shohousenTextContentDispToData(content){
    if (content.startsWith("院外処方")) {
        return content.replace(/ /ug, "　"); // replace ascii space to zenkaku space
    } else {
        return content;
    }
}

export function shohousenTextContentDataToDisp(content){
    if (content.startsWith("院外処方")) {
        return content.replace(/\u{3000}/ug, " "); // replace zenkaku space to ascii space
    } else {
        return content;
    }
}

export function textRep(content){
    content = shohousenTextContentDataToDisp(content);
    return content.replace(/\r\n|\n|\r/g, "<br/>\n");

}
