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
    req.futanWari = await rest.calcFutanWari(req.hoken, rcptAge, visit);
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
    return dateToSqldate(new Date());
}

export function dateToSqldate(date){
    let year = date.getFullYear();
    let month = ("" + (date.getMonth()+1)).padStart(2, "0");
    let day = ("" + date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

export function getLastDayOfMonth(date){
    let d = new Date(date.getFullYear(), date.getMonth() + 1, 0);
    return d.getDate();
}

export function createCheckbox(label, value, data=null){
    let e = document.createElement("div");
    let check = document.createElement("input");
    check.type = "checkbox";
    check.value = value;
    if( data ){
        check.data = data;
    }
    let sep = document.createTextNode(" ");
    let span = document.createElement("span");
    span.innerText = label;
    span.onclick = event => check.click();
    e.append(check, sep, span);
    return e;
}

export function formatDate(date){
    let year = date.getFullYear();
    let month = date.getMonth()+1;
    let day = date.getDate();
    return `${year}年${month}月${day}日`;
}

export function formatDatePadded(date){
    let year = date.getFullYear();
    let month = ("" + (date.getMonth()+1)).padStart(2, "0");
    let day = ("" + date.getDate()).padStart(2, "0");
    return `${year}年${month}月${day}日`;
}

export function diseaseEndReasonToRep(endReason){
    switch(endReason){
        case "N": return "継続";
        case "S": return "中止";
        case "C": return "治癒";
        case "D": return "死亡";
        default: return "????";
    }
}

function adjustDiseaseEndDate(disease){
    if( disease.endReason === "N" ){
        disease.endDate = "0000-00-00";
    }
}

export function composeModifyDiseaseReq(diseaseId, patientId, shoubyoumeicode, startDate, endReason,
                                        endDate, shuushokugocodes){
    let disease = {
        diseaseId,
        patientId,
        shoubyoumeicode,
        startDate,
        endReason,
        endDate
    };
    adjustDiseaseEndDate(disease);
    return {
        disease,
        shuushokugocodes
    };
}

export function getAdjMasterSusp(){
    return {
        shuushokugocode: 8002,
        name: "の疑い"
    };
}

let diseaseExamplesCache = null;

export async function getDiseaseExamples(rest){
    if( !diseaseExamplesCache ){
        diseaseExamplesCache = await rest.listDiseaseExample();
    }
    return diseaseExamplesCache;
}

export async function fillSelectWithDiseaseExampleOptions(selectElement, rest){
    let examples = await getDiseaseExamples(rest);
    selectElement.innerHTML = "";
    for(let ex of examples){
        let opt = createOption(ex.label || ex.byoumei, ex);
        opt.dataset.kind = "example";
        selectElement.append(opt);
    }
}

export async function searchDisease(text, at, searchMode, selectElement, rest){
    if( searchMode === "byoumei" ) {
        let masters = await rest.searchByoumeiMaster(text, at);
        selectElement.innerHTML = "";
        masters.forEach(m => {
            let opt = createOption(m.name, m);
            opt.dataset.kind = "byoumei";
            selectElement.append(opt);
        });
    } else if( searchMode === "adj" ){
        let masters = await rest.searchShuushokugoMaster(text, at);
        selectElement.innerHTML = "";
        masters.forEach(m => {
            let opt = createOption(m.name, m);
            opt.dataset.kind = "adj";
            selectElement.append(opt);
        });
    }
}

export function setupSearchDiseaseResultHandler(selectElement,
                                                byoumeiHandler, shuushokugoHandler, exampleHandler){
    selectElement.onchange = event => {
        let opt = selectElement.querySelector("option:checked");
        if( opt ){
            let kind = opt.dataset.kind;
            if( kind === "byoumei" ){
                let shoubyoumeiMaster = opt.data;
                byoumeiHandler(shoubyoumeiMaster);
            } else if( kind ==="adj" ){
                let shuushokugoMaster = opt.data;
                shuushokugoHandler(shuushokugoMaster);
            } else if( kind === "example" ){
                let example = opt.data;
                exampleHandler(example);
            }
        }
    };
}

export async function resolveMastersOfDiseaseExample(example, at, rest){
    let exByoumeiMaster = null;
    let exAdjMasters = [];
    if( !at ){
        at = todayAsSqldate();
    }
    if( example.byoumei ){
        let master = await rest.findByoumeiMasterByName(example.byoumei, at);
        if( !master ){
            alert(`傷病名（${example.byoumei}）を見つけられませんでした。`);
            return;
        }
        exByoumeiMaster = master;
    }
    if( example.adjList ) {
        for (let adj of example.adjList) {
            let master = await rest.findShuushokugoMasterByName(adj);
            if (!master) {
                alert(`修飾語（${adj}）を見つけられませんでした。`);
                return;
            }
            exAdjMasters.push(master);
        }
    }
    return {
        byoumeiMaster: exByoumeiMaster,
        adjMasters: exAdjMasters
    };
}

export async function confirmDiseaseMasters(byoumeiMaster, adjMasters, at, rest){
    if( byoumeiMaster ){
        let m = await rest.getByoumeiMaster(byoumeiMaster.shoubyoumeicode, at);
        if( !m ){
            return `${byoumeiMaster.name}は${at}に有効でありません。`;
        }
    }
    if( adjMasters ){
        for(let adj of adjMasters){
            let m = await rest.getShuushokugoMaster(adj.shuushokugocode, at);
            if( !m ){
                return `${adj.name}は${at}に有効でありません。`;
            }
        }
    }
    return null;
}

export function splitToLinesWithSep(text){
    let result = [];
    let re = /\r\n|\r|\n/g;
    let i = 0;
    let m;
    while( m = re.exec(text) ){
        let line = text.substring(i, m.index);
        let sep = m[0];
        result.push({line, sep});
        i = m.index + sep.length;
    }
    if( i < text.length ){
        result.push({line: text.substring(i), sep: ""});
    }
    return result;
}

export function mapLine(f, lines){
    return splitToLinesWithSep(lines).map(x => f(x.line) + x.sep).join("");
}

