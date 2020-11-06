import * as F from "../functions.js";

export function compressInternalSpaces(line){
    let m = /^(\s*)(.*)/.exec(line);
    if( m ){
        let lead = m[1];
        let body = m[2];
        body = body.replace(/\s+/g, "　");
        return lead + body;
    } else {
        return line;
    }

}

export function formatPrescForDisp(prescText) {
    return F.mapLine(s => compressInternalSpaces(s), prescText);
}