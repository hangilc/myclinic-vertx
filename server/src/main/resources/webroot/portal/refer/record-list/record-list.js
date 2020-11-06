import {Component} from "../component.js";
import {parseElement} from "../../js/parse-element.js";
import {navFactory} from "./nav.js";
import {recordFactory} from "./record.js";

let template = `
    <div>
        <div class="x-nav-wrapper"></div>
        <div class="x-records-wrapper"></div>
    </div>
`;

export class RecordList extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.nav = navFactory.create(rest);
        this.nav.appendTo(map.navWrapper);
        this.recordWrapperElement = map.recordsWrapper;
    }

    init(){
        super.init();
        this.nav.onChange(page => this.update(page));
        return this;
    }

    set(patientId){
        super.set();
        this.patientId = patientId;
        return this;
    }

    async update(page){
        let visitPage = await this.rest.listVisit(this.patientId, page - 1);
        this.nav.set(visitPage.page + 1, visitPage.totalPages);
        if( visitPage.totalPages > 1 ){
            this.nav.show();
        }
        this.recordWrapperElement.html("");
        for(let visitFull of visitPage.visits){
            let record = recordFactory.create(visitFull, this.rest);
            record.appendTo(this.recordWrapperElement);
        }
    }
}

class RecordListFactory {
    create(patientId, rest){
        let ele = $(template);
        let map = parseElement(ele);
        let comp = new RecordList(ele, map, rest);
        comp.init();
        comp.set(patientId);
        return comp;
    }
}

export let recordListFactory = new RecordListFactory();

