import {PatientManip} from "./patient-manip.js";
import {Disease} from "./disease/disease.js";
import {on} from "../../js/dom-helper.js";

let html = `
<div class="pane practice">
    <div class="row" id="practice-top">
        <div class="col-xl-9">
            <div class="row">
                <h3 class="col-xl-2">診察</h3>
                <div class="col-xl-10 form-inline">
                    <div id="patient-selector-menu-placeholder"></div>
                    <a href="javascript:void(0)" class="ml-2" id="practice-registered-drug-link">登録薬剤</a>
                    <a href="javascript:void(0)" class="ml-2" id="practice-search-text-globally">全文検索</a>
                </div>
            </div>
            <div id="practice-patient-info" class="patient-listener mx-2 my-2"></div>
            <div id="practice-patient-manip" class="patient-listener mx-2 mb-2 form-inline"></div>
            <div id="practice-patient-manip-workarea"></div>
            <div class="practice-nav record-page-listener session-listener d-none mt-2"></div>
            <div id="practice-record-wrapper" class="record-page-listener session-listener"></div>
            <div class="practice-nav record-page-listener session-listener d-none mt-2"></div>
        </div>
        <div class="col-xl-3">
            <div id="practice-right-bar">
                <div id="practice-disease-wrapper" class="session-listener mb-3"></div>
                <div id="practice-no-pay-list-wrapper" class="session-listener no-pay-list-listener"></div>
                <div id="practice-general-workarea"></div>
            </div>
        </div>
    </div>
</div>
`;

export function getHtml() {
    return html;
}

export async function initLayout(pane, rest, controller, printAPI) {
    let {PatientDisplay} = await import("./patient-display.js");
    let {Record} = await import("./record.js");
    let {Nav} = await import("../../components/nav.js");
    let {SearchTextGloballyDialog} = await import("./search-text-globally-dialog.js");
    let {RegisteredDrugDialog} = await import("./registered-drug-dialog/registered-drug-dialog.js")
    let {NoPayList} = await import("./no-pay-list.js");
    let {PatientSelectorMenu} = await import("./patient-selector/patient-selector-menu.js");
    let {replaceNode, show, hide} = await import("../../js/dom-helper.js");
    let app = await import("./app.js");
    let DiseaseAdd = await import("./disease/add.js");
    let DiseaseModify = await import("./disease/modify.js");

    app.init(rest, printAPI, pane, {
        generalWorkarea: document.getElementById("practice-general-workarea"),
        patientManipWorkarea: document.getElementById("practice-patient-manip-workarea")
    });

    {
        let menu = new PatientSelectorMenu();
        replaceNode(document.getElementById("patient-selector-menu-placeholder"), menu.ele);
    }

    {
        let e = document.getElementById("practice-patient-info");
        e.addEventListener("patient-changed", event => {
            let patient = app.patient;
            if (patient) {
                let disp = new PatientDisplay(patient);
                e.innerHTML = "";
                e.append(disp.ele);
            } else {
                e.innerHTML = "";
            }
        });
    }

    {
        let e = document.getElementById("practice-patient-manip");
        e.addEventListener("patient-changed", event => {
            e.innerHTML = "";
            if (app.patient) {
                let manip = new PatientManip();
                e.append(manip.ele);
            }
        })
    }

    document.querySelectorAll(".practice-nav").forEach(e => {
        let nav = new Nav(e);
        nav.setTriggerFun(async page => await app.loadRecordPage(page));
        e.addEventListener("record-page-loaded", event => {
            let page = event.detail;
            if (page.totalPages <= 1) {
                hide(e);
            } else {
                nav.adaptToPage(page.page, page.totalPages);
                show(e);
            }
        });
        e.addEventListener("session-ended", event => hide(e));
    });

    {
        let e = document.getElementById("practice-record-wrapper");
        e.addEventListener("record-page-loaded", async event => {
            let recordPage = event.detail;
            e.innerHTML = "";
            for (let visitFull of recordPage.visits) {
                let record = new Record(visitFull);
                e.append(record.ele);
            }
        });

        e.addEventListener("session-ended", event => {
            e.innerHTML = "";
        });
    }

    {
        let e = document.getElementById("practice-disease-wrapper");
        e.addEventListener("session-started", event => {
            let d = new Disease();
            e.innerHTML = "";
            e.append(d.ele);
        });
        e.addEventListener("session-ended", event => {
                e.innerHTML = "";
            }
        );
    }

    {
        const examples = await rest.listDiseaseExample();
        DiseaseAdd.initExamples(examples);
        DiseaseModify.initExamples(examples);
    }

    document.getElementById("practice-registered-drug-link").addEventListener("click", async _ => {
        let dialog = new RegisteredDrugDialog(rest);
        await dialog.open();
    });

    document.getElementById("practice-search-text-globally").addEventListener("click", async event => {
        let dialog = new SearchTextGloballyDialog();
        await dialog.open(() => dialog.initFocus());
    });

    {
        const e = document.getElementById("practice-no-pay-list-wrapper");
        on(e, "session-ended", event => e.innerHTML = "");
        on(e, "no-pay-list-changed", event => {
            let noPay = e.querySelector(".practice-no-pay-list");
            if( !noPay ){
                noPay = (new NoPayList()).ele;
                e.append(noPay);
            }
            noPay.dispatchEvent(new Event("update-ui"));
        });
    }

}
