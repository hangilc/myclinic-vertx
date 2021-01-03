
export let rest;
export let printAPI;
export let pane = null;
export let map = {};
export let patient = null;
export let currentVisitId = 0;
export let tempVisitId = 0;
export let page = 0;

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
    currentVisitId = visitId;
    tempVisitId = 0;
    console.log("session-started", patientId, visitId);
    publish("session-listener", "session-started", null);
}

export function endSession(){
    patient = null;
    currentVisitId = 0;
    tempVisitId = 0;
    publish("session-listener", "session-ended", null);
}

function findRecordElement(visitId){
    return pane.querySelector(`.practice-record[data-visit-id='${visitId}']`);
}

function publish(subscriber, event, detail, filter){
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
export function init(rest_, printAPI_, pane_, map_){
    rest = rest_;
    printAPI = printAPI_;
    pane = pane_;
    map = map_;
    patient = null;
    currentVisitId = 0;
    tempVisitId = 0;
    page = 0;
}