import {Widget} from "../components/widget.js";
import {BasicInfo} from "./basic-info/basic-info.js";
import {parseElement} from "../js/parse-node.js";
import {NewShahokokuhoBox} from "./new-shahokokuho-box.js";
import {NewKoukikoureiBox} from "./new-koukikourei-box.js";
import {createElementFrom} from "../js/create-element-from.js";
import {validFromRep, validUptoRep} from "../components/form-util.js";
import {NewKouhiBox} from "./new-kouhi-box.js";
import {AllHokenBox} from "./all-hoken-box.js";
import {HokenItemList} from "./hoken-item-list.js";

let tmpl = `
<div class="x-basic mb-2"></div>
<div class="x-current-hoken mb-2"></div>
<div class="mb-2 text-right">
    <button class="btn btn-primary x-start-visit">診察受付</button>
    <button class="btn btn-secondary btn-sm x-close">閉じる</button>
</div>
<div class="mb-2">
    <button class="x-new-shahokokuho btn btn-link mr-1">新規社保国保</button>
    <button class="x-new-koukikourei btn btn-link mr-1">新規後期高齢</button>
    <button class="x-new-kouhi btn btn-link mr-4">新規公費負担</button>
    <button class="x-list-all-hoken btn btn-link">全保険リスト</button>
</div>
<div class="x-all-hoken mb-2"></div>
<div class="x-workarea"></div>
`;

export class PatientWidget extends Widget {
    constructor(patient, rest) {
        super(`${patient.lastName}${patient.firstName}（${patient.patientId}）`);
        this.ele.classList.add(`patient-widget-${patient.patientId}`);
        this.patient = patient;
        this.rest = rest;
        this.getContent().innerHTML = tmpl;
        this.map = parseElement(this.getContent());
        this.map.startVisit.addEventListener("click", async event => await this.doStartVisit());
        this.map.close.addEventListener("click", event => this.ele.remove());
        this.map.newShahokokuho.addEventListener("click", event => this.doNewShahokokuho());
        this.map.newKoukikourei.addEventListener("click", event => this.doNewKoukikourei());
        this.map.newKouhi.addEventListener("click", event => this.doNewKouhi());
        this.map.listAllHoken.addEventListener("click", async event => await this.doListAllHoken());
        this.ele.addEventListener("hoken-changed", async event => await this.refreshHokenItemLists());
        this.currentHokenItemList = new HokenItemList(this.patient.patientId,
            this.map.currentHoken, this.map.workarea, true, this.rest);
        this.allHokenItemList = null;
        this.addBasic();
    }

    async init() {
        await this.refreshHokenItemLists();
    }

    async refreshHokenItemLists() {
        await this.currentHokenItemList.populate();
        if (this.allHokenItemList) {
            await this.allHokenItemList.populate();
        }
    }

    addBasic() {
        let basic = new BasicInfo(this.patient, this.rest);
        basic.ele.addEventListener("patient-updated", event => {
            this.patient = event.detail;
            this.addBasic();
        });
        this.map.basic.innerHTML = "";
        this.map.basic.appendChild(basic.ele);
    }

    async doStartVisit(){
        let patientId = this.patient.patientId;
        await this.rest.startVisit(patientId);
        this.ele.remove();
    }

    doNewShahokokuho() {
        let e = this.ele.querySelector(".shahokokuho-box-new");
        if (e) {
            this.map.workarea.prepend(e);
        } else {
            let part = new NewShahokokuhoBox(this.patient.patientId, this.rest);
            part.ele.addEventListener("shahokokuho-entered", event => {
                part.ele.remove();
                this.ele.dispatchEvent(new Event("refresh-hoken"));
            });
            this.map.workarea.prepend(part.ele);
        }
    }

    doNewKoukikourei() {
        let e = this.ele.querySelector(".koukikourei-box-new");
        if (e) {
            console.log("prepending");
            this.map.workarea.prepend(e);
        } else {
            let part = new NewKoukikoureiBox(this.patient.patientId, this.rest);
            part.ele.addEventListener("koukikourei-entered", event => {
                part.ele.remove();
                this.ele.dispatchEvent(new Event("refresh-hoken"));
            });
            this.map.workarea.prepend(part.ele);
        }
    }

    doNewKouhi() {
        let e = this.ele.querySelector(".kouhi-box-new");
        if (e) {
            console.log("prepending");
            this.map.workarea.prepend(e);
        } else {
            let part = new NewKouhiBox(this.patient.patientId, this.rest);
            part.ele.addEventListener("kouhi-entered", event => {
                part.ele.remove();
                this.ele.dispatchEvent(new Event("refresh-hoken"));
            });
            this.map.workarea.prepend(part.ele);
        }
    }

    async doListAllHoken() {
        if (!this.allHokenItemList) {
            let box = new AllHokenBox(this.patient.patientId, this.rest);
            box.ele.addEventListener("close", event => {
                this.allHokenItemList = null;
                box.ele.remove();
            });
            this.allHokenItemList = new HokenItemList(this.patient.patientId,
                box.getContent(), this.map.workarea, false, this.rest);
            await this.allHokenItemList.populate();
            this.map.allHoken.innerHTML = "";
            this.map.allHoken.append(box.ele);
        }
    }
}

class CurrentHokenItem {
    static tmpl = `
        <div>
            <span class="x-rep"></span>
            <button class="btn btn-link py-0 x-detail">詳細</button>
        </div>
    `;

    constructor(rep, validFrom, validUpto) {
        this.ele = createElementFrom(CurrentHokenItem.tmpl);
        this.map = parseElement(this.ele);
        let from = validFromRep(validFrom);
        let upto = validUptoRep(validUpto);
        this.map.rep.innerText = `${rep} ${from} - ${upto}`;
        this.map.detail.addEventListener("click", event => {
            this.ele.dispatchEvent(new Event("detail"));
        });
    }
}

