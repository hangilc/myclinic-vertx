import {Widget} from "./widget.js";
import {PatientDisp} from "./patient-disp.js";
import {parseElement} from "../js/parse-element.js";
import {compareBy} from "../js/general-util.js";
import * as kanjidate from "../js/kanjidate.js";
import {HokenHelper} from "./hoken-helper.js";

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
        this.closeElement = map.close;
        this.editBasicElement = map.editBasic;
        this.workareaElement = map.workarea;
        this.newShahokokuhoElement = map.newShahokokuho;
        this.newKoukikoureiElement = map.newKoukikourei;
        this.newKouhiElement = map.newKouhi;
        this.patientEditWidget = null;
    }

    init(patientEditWidgetFactory, shahokokuhoNewWidgetFactory, koukikoureiNewWidgetFactory,
         kouhiNewWidgetFactory, shahokokuhoDispWidgetFactory){
        super.init();
        this.patientEditWidgetFactory = patientEditWidgetFactory;
        this.shahokokuhoNewWidgetFactory = shahokokuhoNewWidgetFactory;
        this.koukikoureiNewWidgetFactory = koukikoureiNewWidgetFactory;
        this.kouhiNewWidgetFactory = kouhiNewWidgetFactory;
        this.shahokokuhoDispWidgetFactory = shahokokuhoDispWidgetFactory;
        this.disp.init();
        this.setupDispConverters(this.disp);
        this.currentOnlyElement.on("change", event =>
            this.doCurrentOnlyChanged(this.currentOnlyElement.is(":checked")));
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

    doNewShahokokuho(){
        let widget = this.shahokokuhoNewWidgetFactory.create(this.patient.patientId);
        widget.onEntered(entered => {
            let promise = this.reloadHoken();
            widget.remove();
        })
        widget.prependTo(this.workareaElement);
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

    doEditBasic(){
        if( this.patientEditWidget ){
            this.patientEditWidget.detach().prependTo(this.workareaElement);
        } else {
            let editWidget = this.patientEditWidgetFactory.create(this.patient);
            this.patientEditWidget = editWidget;
            editWidget.onUpdated(updatedPatient => {
                editWidget.remove();
                this.disp.set(updatedPatient);
            });
            editWidget.prependTo(this.workareaElement);
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
            dispWidget.onClose(() => { delete this.shahokokuhoDispWidgetMap[shahokokuho.shahokokuhoId]; });
            this.shahokokuhoDispWidgetMap[shahokokuho.shahokokuhoId] = dispWidget;
        } else {
            dispWidget.detach();
            dispWidget.prependTo(this.workareaElement);
        }
    }

    setHokenList(hokenList){
        let tbody = this.hokenListElement.find("tbody").html("");
        let cmp = compareBy("-validFrom");
        hokenList.shahokokuhoList.sort(cmp);
        for(let shahokokuho of hokenList.shahokokuhoList){
            let tr = this.createTableRow(shahokokuho.rep, formatDate(shahokokuho.validFrom),
                formatDate(shahokokuho.validUpto), honninToKanji(shahokokuho.honnin),
                () => this.doShahokokuhoDetail(shahokokuho), () => {}, () => {});
            tbody.append(tr);
        }
        hokenList.koukikoureiList.sort(cmp);
        for(let koukikourei of hokenList.koukikoureiList){
            let tr = this.createTableRow(koukikourei.rep, formatDate(koukikourei.validFrom),
                formatDate(koukikourei.validUpto), "",
                () => {}, () => {}, () => {});
            tbody.append(tr);
        }
        hokenList.roujinList.sort(cmp);
        for(let roujin of hokenList.roujinList){
            let tr = this.createTableRow(roujin.rep, formatDate(roujin.validFrom),
                formatDate(roujin.validUpto), "",
                () => {}, () => {}, () => {});
            tbody.append(tr);
        }
        hokenList.kouhiList.sort(cmp);
        for(let kouhi of hokenList.kouhiList){
            let tr = this.createTableRow(kouhi.rep, formatDate(kouhi.validFrom),
                formatDate(kouhi.validUpto), "",
                () => {}, () => {}, () => {});
            tbody.append(tr);
        }
    }

    setupDispConverters(disp){
        disp.setBirthdayConv(birthday => this.disp.birthdayAsKanji(birthday, {
            suffix: "生"
        }) + " " + this.disp.calcAge(birthday) + "才");
        disp.setSexConv(sex => this.disp.sexAsKanji(sex));
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