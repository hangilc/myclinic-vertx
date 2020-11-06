import {parseElement} from "../../js/parse-element.js";
import {populateTitle} from "./title.js";
import {createText} from "./text.js";
import {populateTextCommands} from "./text-commands.js";
import {populateHoken} from "./hoken.js";
import {populateDrugs, addDrugs} from "./drugs.js";
import {populateShinryouCommands} from "./shinryou-commands.js";
import {populateShinryouList, addShinryouList} from "./shinryou-list.js";
import {populateConductCommands} from "./conduct-commands.js";
import {populateConducts, addConducts} from "./conducts.js";
import {createTextEnter} from "./text-enter.js";
import {createSendFax} from "./send-fax.js";
import {createShinryouAddRegular} from "./shinryou-add-regular.js";
import * as F from "../functions.js";

let html = `
<div class="x-title title"></div>
<div class="x-left left">
    <div class="x-texts texts"></div>
    <div class="x-text-commands"></div>
</div>
<div class="x-right right">
    <div class="x-hoken"></div>
    <div class="x-drugs"></div>
    <div class="x-shinryou-commands"></div>
    <div class="x-shinryou-workarea"></div>
    <div class="x-shinryou-list"></div>
    <div class="x-conduct-commands"></div>
    <div class="x-conducts"></div>
</div>
`;

export function createRecord(visitFull, rest){
    let visit = visitFull.visit;
    let ele = document.createElement("div");
    ele.dataset.visitId = visitFull.visit.visitId;
    ele.classList.add("record");
    ele.innerHTML = html;
    let map = parseElement(ele);
    populateTitle(map.title, visit.visitedAt, visit.visitId);
    map.texts.innerHTML = "";
    for(let text of visitFull.texts){
        let t = createText(text, rest);
        map.texts.append(t);
    }
    populateTextCommands(map.textCommands);
    populateHoken(map.hoken, visitFull.hoken, visit, rest);
    populateDrugs(map.drugs, visitFull.drugs);
    populateShinryouCommands(map.shinryouCommands, map.shinryouWorkarea, visit.visitId,
        visit.visitedAt, rest);
    populateShinryouList(map.shinryouList, visitFull.shinryouList, rest);
    populateConductCommands(map.conductCommands);
    populateConducts(map.conducts, visitFull.conducts);
    ele.addEventListener("do-enter-text", event => doEnterText(map.texts, visit.visitId, rest));
    ele.addEventListener("text-entered", event => {
        event.stopPropagation();
        map.texts.append(createText(event.detail, rest));
    });
    ele.addEventListener("do-shohousen-fax",
        async event => await doShohousenFax(visit.visitId, visit.visitedAt, map.texts, rest));
    ele.addEventListener("hoken-updated", event => {
        event.stopPropagation();
        let hoken = event.detail;
        map.hoken.innerHTML = "";
        populateHoken(map.hoken, hoken, visit, rest);
    });
    ele.addEventListener("add-regular-shinryou", event => {
        let w = createShinryouAddRegular(visit.visitId, rest);
        w.addEventListener("cancel", event => {
            event.stopPropagation();
            w.remove();
        });
        map.shinryouWorkarea.append(w);
    });
    ele.addEventListener("batch-entered", event => {
        let data = event.detail;
        addShinryouList(map.shinryouList, data.shinryouFulls || [], rest);
        addDrugs(map.drugs, data.drugFulls || []);
        addConducts(map.conducts, data.conductFulls || []);
    });
    return ele;
}

async function doShohousenFax(visitId, visitedAt, wrapper, rest){
    let texts = await rest.listText(visitId);
    let shohousen;
    let pharmaData;
    for(let text of texts){
        let content = text.content;
        if( isShohousen(content) ){
            if( shohousen ){
                alert("処方箋が複数あります。");
                return;
            }
            shohousen = text;
            continue;
        }
        let match = checkPharmacyText(content);
        if( match ){
            if( pharmaData ){
                alert("薬局情報が複数あります。");
                return;
            }
            pharmaData = match;
        }
    }
    if( !shohousen ){
        alert("処方箋がみつかりません。");
        return;
    }
    if( !pharmaData ){
        alert("薬局情報がみつかりません。");
        return;
    }
    let textId = shohousen.textId;
    let date = visitedAt.substring(0, 10);
    let pdfFilePath = await rest.probeShohousenFaxImage(textId, date);
    if( !pdfFilePath ){
        if( !confirm("送信用の処方箋イメージを作成しますか？") ){
            return;
        }
        pdfFilePath = await saveShohousenFaxImage(shohousen, {color: "black"}, rest);
    }
    let sendFax = createSendFax(pdfFilePath, pharmaData.faxNumber, pharmaData.pharmaName, rest);
    sendFax.addEventListener("cancel", event => {
        event.stopPropagation();
        sendFax.remove();
    });
    sendFax.addEventListener("fax-started", event => sendFax.remove());
    wrapper.append(sendFax);
}

async function saveShohousenFaxImage(text, reqOpts, rest){
    let visit = await rest.getVisit(text.visitId);
    let visitDate = visit.visitedAt.substring(0, 10);
    let req = {};
    req.clinicInfo = await rest.getClinicInfo();
    req.hoken = await rest.getHoken(text.visitId);
    req.patient = await rest.getPatient(visit.patientId);
    let rcptAge = await rest.calcRcptAge(req.patient.birthday, visitDate);
    req.futanWari = await rest.calcFutanWari(req.hoken, rcptAge);
    req.issueDate = visitDate;
    req.drugs = text.content;
    Object.assign(req, reqOpts);
    return await rest.saveShohousenPdf(req, text.textId);
}

function isShohousen(content){
    return content.startsWith("院外処方");
}

function checkPharmacyText(content){
    let regex = /(.+)にファックス（(\+\d+)）で送付/;
    let matches = regex.exec(content);
    if( matches ){
        return {
            pharmaName: matches[1],
            faxNumber: matches[2]
        };
    } else {
        return null;
    }
}

function doEnterText(wrapper, visitId, rest){
    let e = createTextEnter(visitId, rest);
    e.addEventListener("text-entered", event => e.remove());
    e.addEventListener("cancel", event => {
        event.stopPropagation();
        e.remove();
    });
    wrapper.append(e);
}

