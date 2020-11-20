import * as kanjidate from "./kanjidate.js";

export function getTimestamp() {
    return kanjidate.getTimestamp();
}

export function getFileExtension(filename) {
    let i = filename.lastIndexOf(".");
    if (i < 0) {
        return "";
    } else {
        return filename.substring(i);
    }
}

export function createPaperScanFileName(patientId, tag, timestamp, ser, ext) {
    if( ser == null || ser === "" ){
        ser = "";
    } else {
        ser = `-${ser}-`;
    }
    return `${patientId}-${tag}-${timestamp}${ser}${ext}`;
}