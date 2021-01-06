
export let rest;
export let printAPI;
export let pane = null;
export let map = {};
export let patient = null;
export let currentVisitId = 0;
export let tempVisitId = 0;
export let currentPage = 0;

export function getTargetVisitId(){
    if( currentVisitId > 0 ){
        return currentVisitId;
    } else {
        return tempVisitId;
    }
}

export function isCurrentOrTempVisitId(visitId){
    return visitId !== 0 && (currentVisitId === visitId || tempVisitId === visitId);
}

export function confirmManip(visitId, question){
    if( visitId === 0 ){
        throw new Error("invalid VisitId");
    }
    if( isCurrentOrTempVisitId(visitId) ){
        return true;
    } else {
        return confirm(`現在診察中でありませんが、${question}？`);
    }
}

export async function startSession(patientId, visitId=0){
    if( visitId > 0 ){
        await rest.startExam(visitId);
    }
    patient = await rest.getPatient(patientId);
    publish("patient-listener", "patient-changed");
    currentVisitId = visitId;
    tempVisitId = 0;
    publish("session-listener", "session-started");
    await loadRecordPage(0);
    await loadDiseases();
}

export function endSession(){
    patient = null;
    publish("patient-listener", "patient-changed");
    currentVisitId = 0;
    tempVisitId = 0;
    currentPage = 0;
    publish("session-listener", "session-ended");
}

export async function loadRecordPage(page){
    let recordPage = await rest.listVisit(patient.patientId, page);
    while( page > 0 && recordPage.visits.length === 0 ){
        page -= 1;
        recordPage = await rest.listVisit(prop.patient.patientId, page);
    }
    currentPage = page;
    publish("record-page-listener", "record-page-loaded", recordPage);
    await batchUpdatePayments(recordPage.visits.map(visitFull => visitFull.visit.visitId));
}

export async function loadDiseases(){
    let diseases = [];
    if( patient ){
        diseases = await rest.listCurrentDisease(patient.patientId);
    }
    publish("disease-listener", "disease-loaded", diseases);
}

export function setTempVisit(visitId){
    tempVisitId = visitId;
    publish("temp-visit-listener", "temp-visit-changed");
}

export function clearTempVisit(text){
    setTempVisit(0);
}

async function batchUpdatePayments(visitIds){
    let map = await rest.batchGetLastPayment(visitIds);
    for(const visitId of Object.keys(map) ){
        let payment = map[visitId];
        publish("payment-listener", "payment-available", payment, {visitId});
    }
}

export function publishPaymentChanged(payment){
    publish("payment-listener", "payment-changed", payment, {visitId: payment.visitId});
}

export function publishTextEntered(text){
    publishToRecord(text.visitId, "text-entered", text);
}

export function publishChargeChanged(charge){
    publish("charge-listener", "charge-changed", charge, {visitId: charge.visitId});
}

export function publishBatchEntered(visitId, entered){
    publishToRecord(visitId, "batch-entered", entered);
}

function findRecordElement(visitId){
    return pane.querySelector(`.practice-record[data-visit-id='${visitId}']`);
}

function publish(subscriber, event, detail=null, filter=null){
    let e = pane;
    if( filter && filter.visitId ){
        e = findRecordElement(filter.visitId);
    }
    if( e ){
        let evt = new CustomEvent(event, {detail: detail});
        if( e.classList.contains(subscriber) ){
            e.dispatchEvent(evt);
        }
        e.querySelectorAll(`.${subscriber}`).forEach(s => s.dispatchEvent(evt));
    }
}

function publishToRecord(visitId, event, detail=null){
    let e = findRecordElement(visitId);
    if( e ){
        e.dispatchEvent(new CustomEvent(event, {detail: detail}));
    }
}

export function init(rest_, printAPI_, pane_, map_){
    rest = rest_;
    printAPI = printAPI_;
    pane = pane_;
    map = map_;
    patient = null;
    currentVisitId = 0;
    tempVisitId = 0;
    currentPage = 0;
}