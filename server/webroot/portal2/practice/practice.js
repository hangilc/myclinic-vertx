import {parseElement} from "../js/parse-element.js";
import {populateTopMenu} from "./top-menu.js";
import {createPatientInfo} from "./patient-info.js";
import {createPatientManip} from "./patient-manip.js";
import {populateRecordNav} from "./record-nav.js";
import {createRecord} from "./record/record.js";
import {createFaxProgress} from "./fax-progress.js";
import * as F from "./functions.js";
import {createText} from "./record/text.js";

let tmpl = `
<h2>診察</h2>
<div class="x-top-menu"> </div>
<div class="main">
    <div class="center">
        <div class="x-patient-info"></div>
        <div class="x-patient-manip"></div>
        <div class="x-upper-nav"></div>
        <div class="x-records records"></div>
        <div class="x-lower-nav"></div>
    </div>
    <div class="right x-main-right"></div>
</div>
`;

class Context {
    constructor(rest, map) {
        this.rest = rest;
        this.map = map;
        this.patient = null;
        this.currentVisitId = 0;
        this.tempVisitId = 0;
        this.patientChangedCallbacks = [];
        this.pageChangedCallbacks = [];
        this.visitIdChangedCallbacks = [];
    }

    registerPatientChangedCallback(cb) {
        this.patientChangedCallbacks.push(cb);
    }

    registerPageChangedCallback(cb) {
        this.pageChangedCallbacks.push(cb);
    }

    registerVisitIdChangedCallback(cb){
        this.visitIdChangedCallbacks.push(cb);
    }

    setPatient(patient) {
        this.patient = patient;
        this.patientChangedCallbacks.forEach(cb => cb(patient));
    }

    setCurrentVisitId(currentVisitId) {
        this.currentVisitId = currentVisitId;
        this.tempVisitId = 0;
        this.visitIdChangedCallbacks.forEach(cb => cb(this.currentVisitId));
    }

    setTempVisitId(tempVisitId) {
        if (this.currentVisitId === 0) {
            this.tempVisitId = tempVisitId;
        } else {
            console.log("cannot set temp visit id (current visit-id is not zero)");
        }
    }

    changePage(currentPage, totalPages) {
        this.pageChangedCallbacks.forEach(cb => cb(currentPage, totalPages));
    }

    async gotoPage(page){
        if( this.patient ){
            let visitPage = await this.rest.listVisit(this.patient.patientId, page);
            this.setRecords(visitPage.visits);
            this.changePage(visitPage.page, visitPage.totalPages);
        }
    }

    setRecords(visits){
        let wrapper = this.map.records;
        wrapper.innerHTML = "";
        visits.forEach(vf => {
            let rec = createRecord(vf, this.rest);
            let visitId = vf.visit.visitId;
            if( visitId === this.currentVisitId ){
                rec.classList.add("current-visit");
            } else if( visitId === this.tempVisitId ){
                rec.classList.add("temp-visit");
            }
            wrapper.append(rec);
        })
    }

    getCopyTarget(){
        return this.currentVisitId || this.tempVisitId;
    }

}

export function createPractice(rest) {
    let ele = document.createElement("div");
    ele.classList.add("practice");
    ele.innerHTML = tmpl;
    let map = parseElement(ele);
    let ctx = new Context(rest, map);
    populateTopMenu(map.topMenu, rest);
    let onPatientChanged = cb => ctx.registerPatientChangedCallback(cb);
    let onPageChanged = cb => ctx.registerPageChangedCallback(cb);
    let onVisitIdChanged = cb => ctx.registerVisitIdChangedCallback(cb);
    onPatientChanged(patient => {
        [map.patientInfo, map.patientManip].forEach(e => {
            e.style.display = patient ? "block" : "none";
        });
    });
    ctx.setPatient(null);
    map.patientInfo.append(createPatientInfo(onPatientChanged));
    map.patientManip.append(createPatientManip(onPatientChanged, onVisitIdChanged));
    populateRecordNav(map.upperNav, onPageChanged);
    populateRecordNav(map.lowerNav, onPageChanged);
    ele.addEventListener("open-patient", event => doOpenPatient(event.detail, ctx));
    ele.addEventListener("do-cashier", event => console.log("do-cashier"));
    ele.addEventListener("goto-page", async event => {
        event.stopPropagation();
        let page = event.detail;
        await ctx.gotoPage(page);
    });
    ele.addEventListener("do-end-patient", async event => await doEndPatient(ctx));
    ele.addEventListener("fax-started", event => {
        event.stopPropagation();
        let data = event.detail;
        let patient = ctx.patient;
        let patientName = `${patient.lastName}${patient.firstName}`;
        let progress = createFaxProgress(patientName, data.faxSid, data.pdfFile, data.faxNumber,
            data.pharmaName, ctx.rest);
        map.mainRight.append(progress);
    });
    ele.addEventListener("do-text-copy-memo", async event => {
        event.stopPropagation();
        let text = event.detail.srcText;
        let onSuccess = event.detail.onSuccess;
        await doTextCopyMemo(text, onSuccess, ele, ctx);
    });
    ele.addEventListener("do-text-copy", async event => {
        event.stopPropagation();
        let text = event.detail.srcText;
        let onSuccess = event.detail.onSuccess;
        await doTextCopy(text, onSuccess, ele, ctx);
    });
    ele.addEventListener("set-temp-visit-id", event => doSetTempVisitId(event.detail, ele, ctx));
    ele.addEventListener("copy-all-shinryou", async event => {
        let visitId = event.detail;
        await doCopyAllShinryou(visitId, ctx);
    });
    ele.addEventListener("shinryou-deleted", event => doShinryouDeleted(event.detail));
    return ele;
}

function findRecordByVisitId(practiceElement, visitId){
    let q = `.record[data-visit-id='${visitId}']`;
    return practiceElement.querySelector(q);
}

function markTempVisit(practiceElement, visitId){
    let e = practiceElement.querySelector(".record.temp-visit");
    if( e ) {
        e.classList.remove("temp-visit");
    }
    let r = findRecordByVisitId(practiceElement, visitId);
    if( r ){
        r.classList.add("temp-visit");
    }
}

function doSetTempVisitId(visitId, practiceElement, ctx){
    if( ctx.currentVisitId ){
        alert("現在診察中なので、暫定診察を設定できません。");
        return;
    }
    ctx.setTempVisitId(visitId);
    markTempVisit(practiceElement, visitId);
}

function findCopyTarget(srcVisitId, what, ctx){
    let targetVisitId = ctx.getCopyTarget();
    if( !targetVisitId ){
        alert(`${what}のコピー先がみつかりません。`);
        return 0;
    }
    if( targetVisitId === srcVisitId ){
        alert(`同じ診察に${what}をコピーすることはできません。`);
        return 0;
    }
    return targetVisitId;
}

function findRecordElement(visitId){
    let q = `.practice .record[data-visit-id='${visitId}']`;
    return document.querySelector(q);
}

async function doCopyAllShinryou(srcVisitId, ctx){
    let targetVisitId = findCopyTarget(srcVisitId, "診療行為", ctx);
    if( !targetVisitId ){
        return;
    }
    let shinryouList = await ctx.rest.listShinryou(srcVisitId);
    let shinryouIds = await ctx.rest.batchCopyShinryou(targetVisitId, shinryouList);
    let entered = await ctx.rest.listShinryouFullByIds(shinryouIds);
    let rec = findRecordElement(targetVisitId);
    if( rec ){
        rec.dispatchEvent(F.event("batch-entered", {shinryouFulls: entered}));
    }
}

function doShinryouDeleted(shinryouId){
    let e = document.querySelector(`.practice .shinryou[data-shinryou-id='${shinryouId}']`);
    if( e ){
        e.remove();
    }
}

async function doTextCopyMemo(text, onSuccess, practiceElement, ctx){
    let targetVisitId = findCopyTarget(text.visitId, "文章", ctx);
    if( !(targetVisitId > 0) ){
        return;
    }
    let memo = F.extractTextMemo(text.content);
    if( !memo ){
        return;
    }
    let textId = await ctx.rest.enterText({
        visitId: targetVisitId,
        content: memo
    });
    let q = `.record[data-visit-id='${targetVisitId}'] .texts`;
    let targetWrapper = practiceElement.querySelector(q);
    if( targetWrapper ){
        let newText = await ctx.rest.getText(textId);
        targetWrapper.append(createText(newText, ctx.rest));
    }
    onSuccess();
}

async function doTextCopy(text, onSuccess, practiceElement, ctx){
    let targetVisitId = findCopyTarget(text.visitId, "文章", ctx);
    if( !(targetVisitId > 0) ){
        return;
    }
    let textId = await ctx.rest.enterText({
        visitId: targetVisitId,
        content: text.content
    });
    let q = `.record[data-visit-id='${targetVisitId}'] .texts`;
    let targetWrapper = practiceElement.querySelector(q);
    if( targetWrapper ){
        let newText = await ctx.rest.getText(textId);
        targetWrapper.append(createText(newText, ctx.rest));
    }
    onSuccess();
}

async function doEndPatient(ctx){
    if( ctx.patient ){
        if( ctx.currentVisitId > 0 ){
            await ctx.rest.suspendExam(ctx.currentVisitId);
        }
        ctx.setPatient(null);
        ctx.setCurrentVisitId(0);
        ctx.changePage(0, 0);
        ctx.setRecords([]);
    }
}

async function closePatient(ctx) {
    if (ctx.patient) {
        if (ctx.currentVisitId > 0) {
            await ctx.rest.suspendExam(ctx.currentVisitId);
        }
        ctx.setCurrentVisitId(0);
        ctx.setPatient(null);
        ctx.changePage(0, 0);
    }
}

async function doOpenPatient(detail, ctx) {
    let patient = detail.patient;
    let visitId = detail.visitId;
    let register = detail.registerForExam;
    await closePatient(ctx);
    if (register && visitId > 0) {
        await ctx.rest.startExam(visitId);
    }
    ctx.setPatient(patient);
    ctx.setCurrentVisitId(visitId);
    let visitsPage = await ctx.rest.listVisit(patient.patientId, 0);
    let visits = visitsPage.visits;
    ctx.setRecords(visits, ctx);
    ctx.changePage(visitsPage.page, visitsPage.totalPages);
}

