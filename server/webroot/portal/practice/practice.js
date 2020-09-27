import * as Layout from "./layout.js";

export function getHtml() {
    return Layout.getHtml();
}

export async function initPractice(pane, rest) {
    await Layout.initLayout(pane, rest);
    pane.addEventListener("start-session", async event => await doStartSession(pane, event.detail, rest));
    pane.addEventListener("goto-page", async event => await fetchRecords(pane, rest, event.detail.patientId,
        event.detail.page));
}

async function fetchRecords(pane, rest, patientId, page) {
    let recordsPage = await rest.listVisit(patientId, page);
    pane.dispatchEvent(new CustomEvent("records-changed", {
        detail: {
            page: recordsPage.page,
            totalPages: recordsPage.totalPages,
            records: recordsPage.visits
        }
    }));
}

async function doStartSession(pane, detail, rest) {
    let patientId = detail.patientId;
    let visitId = detail.visitId;
    pane.dispatchEvent(new CustomEvent("patient-changed", {detail: null}));
    pane.dispatchEvent(new CustomEvent("records-changed", {
        detail: {
            page: 0,
            totalPages: 0,
            records: []
        }
    }));
    let patient = await rest.getPatient(patientId);
    pane.dispatchEvent(new CustomEvent("patient-changed", {detail: patient}));
    await fetchRecords(pane, rest, patientId, 0);
}