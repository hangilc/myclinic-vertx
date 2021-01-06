import {SearchTextDialog} from "./search-text-dialog.js";
import {Component} from "../js/component.js";
import {TitleDisp} from "./title-disp.js";
import {TextDisp} from "./text/text-disp.js";
import {parseElement} from "../../js/parse-node.js";
import {Dialog} from "../../js/dialog2.js";
import {submit, show, hide} from "../../js/dom-helper.js";
import * as app from "./app.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {titleRep} from "./title/title-funs.js";
import {textRep} from "./text/text-funs.js";
import {Nav} from "./nav.js";

let tmpl = `
    <form class="x-search-form form-inline" onsubmit="return false;">
        <input type="text" class="x-search-text form-control"/>
        <button type="submit" class="btn btn-primary ml-2">検索</button>
    </form>
    <div class="x-nav d-none mt-2"></div>
    <div class="x-result" style="width:400px;"></div>
`;

export class SearchTextGloballyDialog extends Dialog {
    constructor() {
        super();
        const self = this;
        this.props = {
            page: 0,
            totalPages: 0,
            get searchText(){ return self.map.searchText.value.trim(); }
        };
        this.setTitle("全文検索");
        this.getBody().innerHTML = tmpl;
        let map = this.map = parseElement(this.getBody());
        this.nav = new Nav(this.map.nav);
        this.nav.setTriggerFun(async page => {
            this.props.page = page;
            await this.doSearch();
        })
        submit(map.searchForm, async event => await this.doSearch());
    }

    initFocus(){
        this.map.searchText.focus();
    }

    async doSearch(){
        const result = await app.rest.searchTextGlobally(this.props.searchText, this.props.page);
        this.props.page = result.page;
        this.props.totalPages = result.totalPages;
        const wrapper = this.map.result;
        wrapper.innerHTML = "";
        result.textVisitPatients.forEach(tvp => {
            const item = new Item(tvp.visit, tvp.text, tvp.patient);
            wrapper.append(item.ele);
        });
        this.updateNav();
    }

    updateNav(){
        if( this.props.totalPages > 1 ){
            this.nav.adaptToPage(this.props.page, this.props.totalPages);
            show(this.map.nav);
        } else {
            hide(this.map.nav);
        }
    }
}

let itemTmpl = `
    <div class="my-2 border border-secondary rounded p-2">
        <div class="bg-light font-weight-bold p-1">
            <div class="x-title-text"></div>
        </div>
        <div class="x-text mt-2"></div>
    </div>
`;

class Item {
    constructor(visit, text, patient) {
        this.ele = createElementFrom(itemTmpl);
        let map = parseElement(this.ele);
        map.titleText.innerText = `(${patient.patientId}) ${patient.lastName}${patient.firstName} `
            + kanjidate.sqldateToKanji(visit.visitedAt.substring(0, 10));
        map.text.innerHTML = textRep(text.content);
    }
}

//
//
// class ItemOrig extends Component {
//     constructor(ele, map, rest) {
//         super(ele, map, rest);
//         this.title = new TitleDisp(map.title_, map.title, rest);
//         this.patientElement = map.title.patient;
//         this.text = new TextDisp(map.text_, map.text, rest);
//     }
//
//     init(){
//         this.title.init();
//         this.text.init();
//     }
//
//     set(textVisitPatient){
//         this.title.set(textVisitPatient.visit.visitedAt);
//         this.patientElement.text(this.createPatientLabel(textVisitPatient.patient));
//         this.text.set(textVisitPatient.text);
//     }
//
//     createPatientLabel(patient){
//         return `${patient.lastName}${patient.firstName} (${patient.patientId})`;
//     }
// }
//
// class SearchTextGloballyDialogOrig extends SearchTextDialog {
//     constructor(ele, map, rest) {
//         super(ele, map, rest);
//         this.globalitemTemplate = map.globalItemTemplate;
//     }
//
//     init(dialogTitle){
//         super.init(dialogTitle);
//         this.itemTemplateHtml = this.globalitemTemplate.html();
//         this.onSearch((text, page) => this.doSearch(text, page));
//     }
//
//     set(){
//         super.set();
//     }
//
//     update(result){
//         let page = result.page;
//         let totalPages = result.totalPages;
//         let textVisitPatients = result.textVisitPatients;
//         this.nav.set(page + 1, totalPages);
//         this.resultElement.html("");
//         for(let textVisitPatient of textVisitPatients){
//             let compItem = this.createItem(textVisitPatient);
//             compItem.appendTo(this.resultElement);
//         }
//     }
//
//     async doSearch(text, page){
//         let result = await this.rest.searchTextGlobally(text, page - 1);
//         this.update(result);
//     }
//
//     createItem(textVisitPatient){
//         let ele = $(this.itemTemplateHtml);
//         let map = parseElement(ele);
//         let compItem = new Item(ele, map, this.rest);
//         compItem.init();
//         compItem.set(textVisitPatient);
//         return compItem;
//     }
// }