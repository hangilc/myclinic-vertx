export async function sendFax(faxNumber, pdfFile, rest) {
    return await rest.sendFax(faxNumber, pdfFile);
}

export async function pollFax(faxSid, addMessage, doneCallback, rest) {
    let status = await rest.pollFax(faxSid);
    addMessage(status);
    if (status === "sending" || status === "processing" || status === "queued") {
        setTimeout(() => pollFax(faxSid, addMessage, doneCallback, rest), 10000);
    } else {
        doneCallback(status);
    }
}

export function startPollFax(faxSid, addMessage, doneCallback, rest) {
    addMessage("started");
    setTimeout(() => pollFax(faxSid, addMessage, doneCallback, rest), 10000);
}

export function reStartPollFax(faxSid, addMessage, doneCallback, rest) {
    addMessage("restarted");
    setTimeout(() => pollFax(faxSid, addMessage, doneCallback, rest), 1000);
}

export function event(name, detail) {
    if (detail) {
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

export function hide(e) {
    e.classList.add("hidden");
}

export function show(e) {
    e.classList.remove("hidden");
}

export function showHide(e, pShow) {
    if (pShow) {
        show(e);
    } else {
        hide(e);
    }
}

export async function createShohousenOps(content, visitId, rest, color = null) { // example -- color: "black"
    let visit = await rest.getVisit(visitId);
    let visitDate = visit.visitedAt.substring(0, 10);
    let req = {};
    req.clinicInfo = await rest.getClinicInfo();
    req.hoken = await rest.getHoken(visitId);
    req.patient = await rest.getPatient(visit.patientId);
    let rcptAge = await rest.calcRcptAge(req.patient.birthday, visitDate);
    req.futanWari = await rest.calcFutanWari(req.hoken, rcptAge);
    req.issueDate = visitDate;
    req.drugs = content;
    if (color) {
        req.color = color;
    }
    return await rest.shohousenDrawer(req);
}

export async function createShohousenFax(text, rest) {
    let visit = await rest.getVisit(text.visitId);
    let patient = await rest.getPatient(visit.patientId);
    let name = await rest.convertToRomaji(patient.lastNameYomi + patient.firstNameYomi);
    let savePath = await rest.getShohousenSavePdfPath(name, text.textId,
        patient.patientId, visit.visitedAt.substring(0, 10));
    let stampInfo = await rest.shohousenGrayStampInfo();
    let content = asContentOfData(text.content);
    let ops = await createShohousenOps(content, text.visitId, rest, "black");
    await rest.saveDrawerAsPdf([ops], "A5", savePath, {stamp: stampInfo});

    function asContentOfData(content) {
        if (content.startsWith("院外処方")) {
            return content.replace(/ /ug, "　"); // replace ascii space to zenkaku space
        } else {
            return content;
        }
    }
}

export async function extendShahokokuhoRep(shahokokuho, rest){
    shahokokuho.rep = await rest.shahokokuhoRep(shahokokuho);
}

export async function extendKoukikoureiRep(koukikourei, rest){
    koukikourei.rep = await rest.koukikoureiRep(koukikourei);
}

export async function extendRoujinRep(roujin, rest){
    roujin.rep = await rest.roujinRep(roujin);
}

export async function extendKouhiRep(kouhi, rest){
    kouhi.rep = await rest.kouhiRep(kouhi);
}

export function removeChildren(node){
    let result = [];
    while( node.firstChild ){
        result.push(node.firstChild);
        node.firstChild.remove();
    }
    return result;
}

export function addChildren(node, children){
    children.forEach(ch => node.append(ch));
}

export function createOption(label, data){
    let opt = document.createElement("option");
    opt.innerText = label;
    opt.data = data;
    return opt;
}

export function getShinryouTekiyou(shinryouFull){
    return (shinryouFull && shinryouFull.attr) ? shinryouFull.attr.tekiyou : "";
}

export function createShinryouLabel(masterName, tekiyou){
    if( tekiyou ){
        return `${masterName} ［摘要：${tekiyou}］`;
    } else {
        return masterName;
    }
}

export function setShinryouTekiyou(shinryouFull, tekiyou){
    if( !shinryouFull.attr ){
        shinryouFull.attr = {};
    }
    shinryouFull.attr.tekiyou = tekiyou;
}

export function todayAsSqldate(){
    let d = new Date();
    let year = d.getFullYear();
    let month = ("" + (d.getMonth()+1)).padStart(2, "0");
    let day = ("" + d.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

