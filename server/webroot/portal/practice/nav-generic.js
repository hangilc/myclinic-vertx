import {parseElement} from "../js/parse-element.js";

let html = `
    <a href="javascript:void(0)" class="x-first">最初</a>
    <a href="javascript:void(0)" class="x-prev ml-1">前へ</a>
    <a href="javascript:void(0)" class="x-next ml-1">次へ</a>
    <a href="javascript:void(0)" class="x-last ml-1 mr-1">最後</a>
    [<span class="x-page"></span>/<span class="x-total"></span>]
`;

export class NavGeneric {
    constructor(ele) {
        ele.html(html);
        this.ele = ele;
        let map = parseElement(ele);
        this.firstElement = map.first;
        this.prevElement = map.prev;
        this.nextElement = map.next;
        this.lastElement = map.last;
        this.pageElement = map.page;
        this.totalElement = map.total;
        this.cb = (page, totalPages) => {};
        this._init();
    }

    _init() {
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

    setCallback(cb){
        this.cb = cb;
    }

    triggerGoto(page) {
        this.cb(page, this.getTotalPage());
    }

    show() {
        this.ele.removeClass("d-none");
    }

    hide() {
        this.ele.addClass("d-none");
    }
}