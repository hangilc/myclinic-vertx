import {Component} from "./component.js";
import {parseElement} from "../js/parse-element.js";
import * as consts from "../js/consts.js";
import * as kanjidate from "../js/kanjidate.js";

export class WqueueTable extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.tbodyElement = ele.find("tbody");
        this.itemTemplateHtml = map.itemTemplate.html();
        this.cashierButtonTemplateHtml = map.cashierButtonTemplate.html();
    }

    init(cashierDialogFactory, broadcaster){
        super.init();
        this.cashierDialogFactory = cashierDialogFactory;
        this.broadcaster = broadcaster;
        return this;
    }

    set(wqueueFulls){
        super.set();
        this.tbodyElement.html("");
        if( wqueueFulls ){
            for(let wqueueFull of wqueueFulls){
                let item = this.createItem(wqueueFull);
                this.tbodyElement.append(item);
            }
        }
        return this;
    }

    createItem(wqueueFull){
        let e = $(this.itemTemplateHtml);
        let m = parseElement(e);
        let wqueue = wqueueFull.wqueue;
        let patient = wqueueFull.patient;
        let age = kanjidate.calcAge(patient.birthday);
        m.state.text(consts.wqueueStateCodeToRep(wqueue.waitState));
        m.patientId.text(patient.patientId);
        m.name.text(`${patient.lastName} ${patient.firstName}`);
        m.yomi.text(`${patient.lastNameYomi} ${patient.firstNameYomi}`);
        m.sex.text(consts.sexToRep(patient.sex));
        m.birthday.text(kanjidate.sqldateToKanji(patient.birthday, {padZero: true}));
        m.age.text(age);
        if( wqueue.waitState === consts.WqueueStateWaitCashier ){
            let btn = $(this.cashierButtonTemplateHtml);
            btn.on("click", event => this.doCashier(wqueue.visitId));
            m.manip.prepend(btn);
        }
        m.menu.delete.on("click", event => this.doDelete(wqueueFull));
        return e;
    }

    onChanged(cb){
        this.on("changed", event => cb());
    }

    async doDelete(wqueueFull){
        let patient = wqueueFull.patient;
        if( !confirm(`この受付（${patient.lastName}${patient.firstName}）を削除していいですか？`) ){
            return;
        }
        let visitId = wqueueFull.wqueue.visitId;
        await this.rest.deleteVisitFromReception(visitId);
        this.broadcaster.broadcast("visit-deleted", visitId);
    }

    async doCashier(visitId){
        let charge = await this.rest.getCharge(visitId);
        if( !charge ){
            alert("Cannot find charge.");
            return;
        }
        let meisai = await this.rest.getMeisai(visitId);
        let payments = await this.rest.listPayment(visitId);
        let dialog = this.cashierDialogFactory.create(meisai, visitId, charge.charge, payments);
        let result = await dialog.open();
        if( result){
            this.trigger("changed");
        }
    }
}
