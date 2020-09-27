import * as Layout from "./layout.js";

export function getHtml() {
    return Layout.getHtml();
}

class Controller {
    constructor(pane, rest) {
        this.pane = pane;
        this.rest = rest;
        this.patientId = 0;
        this.visitId = 0;
        this.tempVisitId = 0;
        pane.addEventListener("start-session", async event =>
            await this.startSession(event.detail.patientId, event.detail.visitId));
        pane.addEventListener("goto-page", async event =>
            await this.fetchRecords(event.detail.patientId, event.detail.page));
        pane.addEventListener("change-visit-id", event => this.changeVisitId(event.detail));
        pane.addEventListener("change-temp-visit-id", event => this.changeTempVisitId(event.detail));
    }

    getPatientId() {
        return this.patientId;
    }

    getVisitId() {
        return this.visitId;
    }

    getTempVisitId() {
        return this.tempVisitId;
    }

    setTempVisitId(tempVisitId) {
        if (this.visitId === 0) {
            this.tempVisitId = tempVisitId;
        }
    }

    changeVisitId(visitId){
        if( visitId === 0 ){
            this.visitId = visitId;
        } else {
            if( this.tempVisitId > 0 ){
                throw "Canot change current visitId (tempVisitId is not zero).";
            } else {
                this.visitId = visitId;
            }
        }
        this.pane.dispatchEvent(new CustomEvent("visit-id-changed", { detail: visitId }));
    }

    changeTempVisitId(tempVisitId){
        if( tempVisitId === 0 ){
            this.tempVisitId = tempVisitId;
        } else {
            if( this.visitId > 0 ){
                throw "Cannot change tempVisitId (current visitId is not zero).";
            } else {
                this.tempVisitId = tempVisitId;
            }
        }
        this.pane.dispatchEvent(new CustomEvent("temp-visit-id-changed", { detail: tempVisitId }));
    }

    async startSession(patientId, visitId) {
        this.patientId = patientId;
        this.visitId = visitId;
        this.tempVisitId = 0;
        let patient = await this.rest.getPatient(patientId);
        let pane = this.pane;
        pane.dispatchEvent(new CustomEvent("patient-changed", {detail: patient}));
        await this.fetchRecords(patientId, 0);
    }

    async endSession() {
        this.patientId = 0;
        this.visitId = 0;
        this.tempVisitId = 0;
        let pane = this.pane;
        pane.dispatchEvent(new CustomEvent("patient-changed", {detail: null}));
        pane.dispatchEvent(new CustomEvent("records-changed", {
            detail: {
                page: 0,
                totalPages: 0,
                records: []
            }
        }));
    }

    async fetchRecords(patientId, page) {
        console.log("enter fetchRecords", patientId, page);
        let rest = this.rest;
        let recordsPage = await rest.listVisit(patientId, page);
        console.log("fetched records", recordsPage.visits.length);
        let pane = this.pane;
        pane.dispatchEvent(new CustomEvent("records-changed", {
            detail: {
                page: recordsPage.page,
                totalPages: recordsPage.totalPages,
                records: recordsPage.visits
            }
        }));
    }

}

export async function initPractice(pane, rest) {
    let cont = new Controller(pane, rest);
    await Layout.initLayout(pane, rest, cont);
}
