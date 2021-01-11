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

let lineTermPattern = /\r\n|\n|\r/;

export function extractMemo(content) {
    let lines = content.split(lineTermPattern);
    let memo = [];
    for (let line of lines) {
        if (line.startsWith("●") || line.startsWith("★")) {
            memo.push(line);
        } else {
            break;
        }
    }
    return memo.join("\n");
}

export function hasMemo(content) {
    return content && (content.startsWith("●") || content.startsWith("★"));
}

export function isShohousen(content) {
    return content.startsWith("院外処方");
}

let shohousen0410Pattern = /\n@0410対応/;

export function is0410Shohousen(content){
    return shohousen0410Pattern.test(content);
}



