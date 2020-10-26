import {Widget} from "./widget.js";
import {PatientDisp} from "./patient-disp.js";
import {parseElement} from "../js/parse-element.js";
import {compareBy} from "../js/general-util.js";
import * as kanjidate from "../js/kanjidate.js";
import {HokenHelper} from "./hoken-helper.js";
import {UploadImageDialog} from "./upload-image-dialog.js";
import {PatientEditWidget} from "./patient-edit-widget.js";
import {ShahokokuhoNewWidget} from "./shahokokuho-new-widget.js";

let tableRowHtml = `
<tr>
    <td class="x-rep"></td>
    <td class="x-valid-from"></td>
    <td class="x-valid-upto"></td>
    <td class="x-honnin"></td>
    <td class="x-manip">
        <a href="javascript:void(0)" class="x-detail">詳細</a>
        <a href="javascript:void(0)" class="x-edit">編集</a>
        <a href="javascript:void(0)" class="x-delete">削除</a>
    </td>
</tr>
`;

export class PatientAndHokenEditWidget extends Widget {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.disp = new PatientDisp(map.disp_, map.disp, rest);
        this.hokenListElement = map.hokenList;
        this.currentOnlyElement = map.currentOnly;
        this.registerElement = map.register;
        this.uploadImageElement = map.uploadImage;
        this.closeElement = map.close;
        this.editBasicElement = map.editBasic;
        this.workareaElement = map.workarea;
        this.newShahokokuhoElement = map.newShahokokuho;
        this.newKoukikoureiElement = map.newKoukikourei;
        this.newKouhiElement = map.newKouhi;
        this.patientEditWidget = null;
    }

    init(koukikoureiNewWidgetFactory,
         kouhiNewWidgetFactory, shahokokuhoDispWidgetFactory,
         koukikoureiDispWidgetFactory, roujinDispWidgetFactory,kouhiDispWidgetFactory,
         shahokokuhoEditWidgetFactory, koukikoureiEditWidgetFactory, kouhiEditWidgetFactory,
         broadcaster){
        super.init();
        this.koukikoureiNewWidgetFactory = koukikoureiNewWidgetFactory;
        this.kouhiNewWidgetFactory = kouhiNewWidgetFactory;
        this.shahokokuhoDispWidgetFactory = shahokokuhoDispWidgetFactory;
        this.koukikoureiDispWidgetFactory = koukikoureiDispWidgetFactory;
        this.roujinDispWidgetFactory = roujinDispWidgetFactory;
        this.kouhiDispWidgetFactory = kouhiDispWidgetFactory;
        this.shahokokuhoEditWidgetFactory = shahokokuhoEditWidgetFactory;
        this.koukikoureiEditWidgetFactory = koukikoureiEditWidgetFactory;
        this.kouhiEditWidgetFactory = kouhiEditWidgetFactory;
        this.broadcaster = broadcaster;
        this.disp.init();
        setupDispConverters(this.disp);
        this.currentOnlyElement.on("change", event =>
            this.doCurrentOnlyChanged(this.currentOnlyElement.is(":checked")));
        this.registerElement.on("click", event => this.doRegisterForExam());
        this.uploadImageElement.on("click", event => this.doUploadImage());
        this.closeElement.on("click", event => this.close());
        this.editBasicElement.on("click", event => this.doEditBasic());
        this.newShahokokuhoElement.on("click", event => this.doNewShahokokuho());
        this.newKoukikoureiElement.on("click", event => this.doNewKoukikourei());
        this.newKouhiElement.on("click", event => this.doNewKouhi());
        this.shahokokuhoDispWidgetMap = {};
        this.koukikoureiDispWidgetMap = {};
        this.roujinDispWidgetMap = {};
        this.kouhiDispWidgetMap = {};
        return this;
    }

    set(patient, currentHokenList){
        super.set();
        this.patient = patient;
        this.disp.set(patient);
        this.setHokenList(currentHokenList);
        return this;
    }

    isCurrentOnly(){
        return this.currentOnlyElement.is(":checked");
    }

    getPatientId(){
        return this.patient ? this.patient.patientId : 0;
    }

    async reloadHoken(){
        let helper = new HokenHelper(this.rest);
        if( this.isCurrentOnly() ){
            let hokenList = await helper.fetchAvailableHoken(this.patient.patientId, kanjidate.todayAsSqldate());
            this.setHokenList(hokenList);
        } else {
            let hokenList = await helper.fetchAllHoken(this.patient.patientId);
            this.setHokenList(hokenList);
        }
    }

    async doRegisterForExam(){
        let patientId = this.getPatientId();
        if( patientId > 0 ){
            let visitId = await this.rest.startVisit(patientId);
            this.broadcaster.broadcast("visit-created", visitId);
            alert("診療が受け付けられました。");
        }
    }

    async doUploadImage(){
        let patientId = this.getPatientId();
        if( patientId > 0 ){
            await (new UploadImageDialog(patientId)).open();
        }
    }

    doNewShahokokuho(){
        let widget = new ShahokokuhoNewWidget(this.patient.patientId, this.rest);
        widget.onEntered(async entered => {
            await this.reloadHoken();
            widget.remove();
        })
        widget.prependTo(this.workareaElement.get(0));
    }

    doNewKoukikourei(){
        let widget = this.koukikoureiNewWidgetFactory.create(this.patient.patientId);
        widget.onEntered(entered => {
            let promise = this.reloadHoken();
            widget.remove();
        })
        widget.prependTo(this.workareaElement);
    }

    doNewKouhi(){
        let widget = this.kouhiNewWidgetFactory.create(this.patient.patientId);
        widget.onEntered(entered => {
            let promise = this.reloadHoken();
            widget.remove();
        })
        widget.prependTo(this.workareaElement);
    }

    doEditShahokokuho(shahokokuho){
        let widget = this.shahokokuhoEditWidgetFactory.create(shahokokuho);
        widget.onUpdated(updated => {
            let promise = this.reloadHoken();
            widget.remove();
        })
        widget.prependTo(this.workareaElement);
    }

    doEditKoukikourei(koukikourei){
        let widget = this.koukikoureiEditWidgetFactory.create(koukikourei);
        widget.onUpdated(updated => {
            let promise = this.reloadHoken();
            widget.remove();
        })
        widget.prependTo(this.workareaElement);
    }

    doEditKouhi(kouhi){
        let widget = this.kouhiEditWidgetFactory.create(kouhi);
        widget.onUpdated(updated => {
            let promise = this.reloadHoken();
            widget.remove();
        })
        widget.prependTo(this.workareaElement);
    }

    doEditBasic(){
        if( this.patientEditWidget ){
            let w = this.patientEditWidget;
            let p = this.workareaElement.get(0);
            p.removeChild(w);
            p.prepend(w);
        } else {
            let editWidget = new PatientEditWidget(this.patient, this.rest);
            this.patientEditWidget = editWidget.ele;
            editWidget.onUpdated(updatedPatient => {
                editWidget.close();
                this.patient = updatedPatient;
                this.disp.set(updatedPatient);
            });
            editWidget.onClosed(() => {
                this.patientEditWidget = null;
            });
            this.workareaElement.get(0).prepend(editWidget.ele);
        }
    }

    async doCurrentOnlyChanged(checked){
        let helper = new HokenHelper(this.rest);
        if( checked ){
            let result = await helper.fetchAvailableHoken(this.patient.patientId, kanjidate.todayAsSqldate());
            this.setHokenList(result);
        } else {
            let result = await helper.fetchAllHoken(this.patient.patientId);
            this.setHokenList(result);
        }
    }

    createTableRow(rep, validFrom, validUpto, honninKazoku, detailFun, editFun, deleteFun){
        let ele = $(tableRowHtml);
        let map = parseElement(ele);
        map.rep.text(rep);
        map.validFrom.text(validFrom);
        map.validUpto.text(validUpto);
        map.honnin.text(honninKazoku);
        map.detail.on("click", event => detailFun());
        map.edit.on("click", event => editFun());
        map.delete.on("click", event => deleteFun());
        return ele;
    }

    doShahokokuhoDetail(shahokokuho){
        let dispWidget = this.shahokokuhoDispWidgetMap[shahokokuho.shahokokuhoId];
        if( !dispWidget ){
            dispWidget = this.shahokokuhoDispWidgetFactory.create(shahokokuho);
            dispWidget.prependTo(this.workareaElement);
            dispWidget.onClosed(() => { delete this.shahokokuhoDispWidgetMap[shahokokuho.shahokokuhoId]; });
            this.shahokokuhoDispWidgetMap[shahokokuho.shahokokuhoId] = dispWidget;
        } else {
            dispWidget.detach();
            dispWidget.prependTo(this.workareaElement);
        }
    }

    doKoukikoureiDetail(koukikourei){
        let dispWidget = this.koukikoureiDispWidgetMap[koukikourei.koukikoureiId];
        if( !dispWidget ){
            dispWidget = this.koukikoureiDispWidgetFactory.create(koukikourei);
            dispWidget.prependTo(this.workareaElement);
            dispWidget.onClose(() => { delete this.koukikoureiDispWidgetMap[koukikourei.koukikoureiId]; });
            this.koukikoureiDispWidgetMap[koukikourei.koukikoureiId] = dispWidget;
        } else {
            dispWidget.detach();
            dispWidget.prependTo(this.workareaElement);
        }
    }

    doRoujinDetail(roujin){
        let dispWidget = this.roujinDispWidgetMap[roujin.roujinId];
        if( !dispWidget ){
            dispWidget = this.roujinDispWidgetFactory.create(roujin);
            dispWidget.prependTo(this.workareaElement);
            dispWidget.onClose(() => { delete this.roujinDispWidgetMap[roujin.roujinId]; });
            this.roujinDispWidgetMap[roujin.roujinId] = dispWidget;
        } else {
            dispWidget.detach();
            dispWidget.prependTo(this.workareaElement);
        }
    }

    doKouhiDetail(kouhi){
        let dispWidget = this.kouhiDispWidgetMap[kouhi.kouhiId];
        if( !dispWidget ){
            dispWidget = this.kouhiDispWidgetFactory.create(kouhi);
            dispWidget.prependTo(this.workareaElement);
            dispWidget.onClose(() => { delete this.kouhiDispWidgetMap[kouhi.kouhiId]; });
            this.kouhiDispWidgetMap[kouhi.kouhiId] = dispWidget;
        } else {
            dispWidget.detach();
            dispWidget.prependTo(this.workareaElement);
        }
    }

    async doShahokokuhoDelete(shahokokuho){
        if( confirm("この社保国保を削除しますか？") ){
            let data = Object.assign({}, shahokokuho);
            delete data.rep;
            await this.rest.deleteShahokokuho(data);
            await this.reloadHoken();
        }
    }

    async doKoukikoureiDelete(koukikourei){
        if( confirm("この後期高齢保険を削除しますか？") ){
            let data = Object.assign({}, koukikourei);
            delete data.rep;
            await this.rest.deleteKoukikourei(data);
            await this.reloadHoken();
        }
    }

    async doRoujinDelete(roujin){
        if( confirm("この老人保険を削除しますか？") ){
            let data = Object.assign({}, roujin);
            delete data.rep;
            await this.rest.deleteRoujin(data);
            await this.reloadHoken();
        }
    }

    async doKouhiDelete(kouhi){
        if( confirm("この高位負担を削除しますか？") ){
            let data = Object.assign({}, kouhi);
            delete data.rep;
            await this.rest.deleteKouhi(data);
            await this.reloadHoken();
        }
    }

    setHokenList(hokenList){
        let tbody = this.hokenListElement.find("tbody").html("");
        let cmp = compareBy("-validFrom");
        hokenList.shahokokuhoList.sort(cmp);
        for(let shahokokuho of hokenList.shahokokuhoList){
            let tr = this.createTableRow(shahokokuho.rep, formatDate(shahokokuho.validFrom),
                formatDate(shahokokuho.validUpto), honninToKanji(shahokokuho.honnin),
                () => this.doShahokokuhoDetail(shahokokuho),
                () => this.doEditShahokokuho(shahokokuho),
                () => this.doShahokokuhoDelete(shahokokuho));
            tbody.append(tr);
        }
        hokenList.koukikoureiList.sort(cmp);
        for(let koukikourei of hokenList.koukikoureiList){
            let tr = this.createTableRow(koukikourei.rep, formatDate(koukikourei.validFrom),
                formatDate(koukikourei.validUpto), "",
                () => this.doKoukikoureiDetail(koukikourei),
                () => this.doEditKoukikourei(koukikourei),
                () => this.doKoukikoureiDelete(koukikourei));
            tbody.append(tr);
        }
        hokenList.roujinList.sort(cmp);
        for(let roujin of hokenList.roujinList){
            let tr = this.createTableRow(roujin.rep, formatDate(roujin.validFrom),
                formatDate(roujin.validUpto), "",
                () => this.doRoujinDetail(roujin),
                () => { alert("Not implemented."); },
                () => this.doRoujinDelete(roujin));
            tbody.append(tr);
        }
        hokenList.kouhiList.sort(cmp);
        for(let kouhi of hokenList.kouhiList){
            let tr = this.createTableRow(kouhi.rep, formatDate(kouhi.validFrom),
                formatDate(kouhi.validUpto), "",
                () => this.doKouhiDetail(kouhi),
                () => this.doEditKouhi(kouhi),
                () => this.doKouhiDelete(kouhi));
            tbody.append(tr);
        }
    }


}

function honninToKanji(honnin){
    return honnin ? "本人" : "家族";
}

function formatDate(sqldate){
    if( !sqldate || sqldate === "0000-00-00" ){
        return "";
    } else {
        return kanjidate.sqldateToKanji(sqldate, {padZero: true});
    }
}

function setupDispConverters(disp){
    disp.setBirthdayConv(birthday => disp.birthdayAsKanji(birthday, {
        suffix: "生"
    }) + " " + disp.calcAge(birthday) + "才");
    disp.setSexConv(sex => disp.sexAsKanji(sex));
}

