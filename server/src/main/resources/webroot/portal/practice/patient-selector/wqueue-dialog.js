import {Dialog} from "../../../js/dialog2.js";
import {parseElement} from "../../../js/parse-node.js";
import {PatientDisp} from "./patient-disp.js";
import {click, on} from "../../../js/dom-helper.js";
import {wqueueStateCodeToRep} from "../../js/consts.js";

let bodyTmpl = `
    <div class="row">
        <div class="col-6">
            <select class="form-control x-select" size="7"></select>
        </div>
        <div class="col-6 x-disp"></div>
    </div>
`;

let footerTmpl = `
    <button type="button" class="btn btn-primary x-enter">選択</button>
    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class WqueueDialog extends Dialog {
    constructor(prop, wqueueFulls) {
        super({width: "700px"});
        this.prop = prop;
        this.rest = prop.rest;
        this.patient = null;
        this.visitId = 0;
        this.setTitle("受付患者選択");
        this.getBody().innerHTML = bodyTmpl;
        let bmap = this.bmap = parseElement(this.getBody());
        this.disp = new PatientDisp();
        bmap.disp.append(this.disp.ele);
        on(bmap.select, "change", event => this.doSelect());
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        click(fmap.enter, async event => await this.doEnter());
        click(fmap.cancel, event => this.close());
        this.setSelect(wqueueFulls);
    }

    setSelect(wqueueFulls){
        let select = this.bmap.select;
        select.innerHTML = "";
        wqueueFulls.forEach(wqueueFull => {
            let opt = document.createElement("option");
            opt.innerText = optRep(wqueueFull);
            opt.data = wqueueFull;
            select.append(opt);
        });
    }

    doSelect(){
        let opt = this.bmap.select.querySelector("option:checked");
        if( opt ){
            let wqueueFull = opt.data;
            this.setPatient(wqueueFull.patient);
            this.visitId = wqueueFull.wqueue.visitId;
        }
    }

    setPatient(patient){
        this.patient = patient;
        this.disp.setPatient(patient);
    }

    async doEnter(){
        let patient = this.patient;
        if( !patient ){
            alert("患者が選択されていません。");
            return;
        }
        let visitId = this.visitId;
        if( !(visitId > 0) ){
            alert("No visit selected");
            return;
        }
        this.prop.endSession();
        this.prop.startSession(patient.patientId, visitId);
        this.close();
    }
}

function optRep(wqueueFull){
    let state = wqueueStateCodeToRep(wqueueFull.wqueue.waitState);
    let patient = wqueueFull.patient;
    return `［${state}］${patient.lastName} ${patient.firstName}`;
}