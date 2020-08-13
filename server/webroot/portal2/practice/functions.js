export async function sendFax(faxNumber, pdfFile, rest){
    return await rest.sendFax(faxNumber, pdfFile);
}

export async function pollFax(faxSid, addMessage, doneCallback, rest){
    let status = await rest.pollFax(faxSid);
    addMessage(status);
    if (status === "sending" || status === "processing" || status === "queued") {
        setTimeout(() => pollFax(faxSid, addMessage, doneCallback, rest), 10000);
    } else {
        doneCallback(status);
    }
}

export function startPollFax(faxSid, addMessage, doneCallback, rest){
    addMessage("started");
    setTimeout(() => pollFax(faxSid, addMessage, doneCallback, rest), 10000);
}

export function reStartPollFax(faxSid, addMessage, doneCallback, rest){
    addMessage("restarted");
    setTimeout(() => pollFax(faxSid, addMessage, doneCallback, rest), 1000);
}

export function event(name, detail){
    if( detail ){
        return new CustomEvent(name, {bubbles: true, detail});
    } else {
        return new Event(name, {bubbles: true});
    }
}

export function extractTextMemo(content) {
    let lines = content.split(/\r\n|\n|\r/);
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

