import {Component} from "./component.js";

export class Nav extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.firstElement = map.first;
        this.prevElement = map.prev;
        this.nextElement = map.next;
        this.lastElement = map.last;
        this.pageElement = map.page;
        this.totalElement = map.total;
        this.patientId = 0;
    }

    init() {
        this.set(0, 0);
        this.firstElement.on("click", event => this.gotoPage(0, this.getTotalPage()));
        this.prevElement.on("click", event => this.gotoPage(this.getCurrentPage() - 1, this.getTotalPage()));
        this.nextElement.on("click", event => this.gotoPage(this.getCurrentPage() + 1, this.getTotalPage()));
        this.lastElement.on("click", event => this.gotoPage(this.getTotalPage() - 1, this.getTotalPage()));
    }

    set(page, total) {
        if (page > total) {
            page = total;
        }
        this.setPage(page);
        this.setTotal(total);
        if( total <= 1 ){
            this.hide();
        } else {
            this.show();
        }
    }

    setPage(page) {
        this.pageElement.text(page + 1);
    }

    setTotal(total) {
        this.totalElement.text(total);
    }

    setPatientId(patientId) {
        this.patientId = patientId;
    }

    gotoPage(page) {
        let total = this.getTotalPage();
        if (page >= 0 && page < total && this.patientId > 0) {
            this.triggerGoto(page, total, this.patientId);
        }
    }

    getCurrentPage() {
        return parseInt(this.pageElement.text()) - 1;
    }

    getTotalPage() {
        return parseInt(this.totalElement.text());
    }

    triggerGoto(page) {
        if (this.patientId > 0) {
            this.ele[0].dispatchEvent(new CustomEvent("goto-page", {
                bubbles: true,
                detail: {patientId: this.patientId, page}
            }));
        }
    }

    show() {
        this.ele.removeClass("d-none");
    }

    hide() {
        this.ele.addClass("d-none");
    }

}