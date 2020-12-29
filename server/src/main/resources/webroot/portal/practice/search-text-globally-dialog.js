import {SearchTextDialog} from "./search-text-dialog.js";
import {Component} from "../js/component.js";
import {TitleDisp} from "./title-disp.js";
import {TextDisp} from "./text/text-disp.js";
import {parseElement} from "../js/parse-element.js";

class Item extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.title = new TitleDisp(map.title_, map.title, rest);
        this.patientElement = map.title.patient;
        this.text = new TextDisp(map.text_, map.text, rest);
    }

    init(){
        this.title.init();
        this.text.init();
    }

    set(textVisitPatient){
        this.title.set(textVisitPatient.visit.visitedAt);
        this.patientElement.text(this.createPatientLabel(textVisitPatient.patient));
        this.text.set(textVisitPatient.text);
    }

    createPatientLabel(patient){
        return `${patient.lastName}${patient.firstName} (${patient.patientId})`;
    }
}

export class SearchTextGloballyDialog extends SearchTextDialog {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.globalitemTemplate = map.globalItemTemplate;
    }

    init(dialogTitle){
        super.init(dialogTitle);
        this.itemTemplateHtml = this.globalitemTemplate.html();
        this.onSearch((text, page) => this.doSearch(text, page));
    }

    set(){
        super.set();
    }

    update(result){
        let page = result.page;
        let totalPages = result.totalPages;
        let textVisitPatients = result.textVisitPatients;
        this.nav.set(page + 1, totalPages);
        this.resultElement.html("");
        for(let textVisitPatient of textVisitPatients){
            let compItem = this.createItem(textVisitPatient);
            compItem.appendTo(this.resultElement);
        }
    }

    async doSearch(text, page){
        let result = await this.rest.searchTextGlobally(text, page - 1);
        this.update(result);
    }

    createItem(textVisitPatient){
        let ele = $(this.itemTemplateHtml);
        let map = parseElement(ele);
        let compItem = new Item(ele, map, this.rest);
        compItem.init();
        compItem.set(textVisitPatient);
        return compItem;
    }
}