import {parseElement} from "../../js/parse-node.js";

let tmpl = `
    <a href="javascript:void(0)" class="x-first">最初</a>
    <a href="javascript:void(0)" class="x-prev ml-1">前へ</a>
    <a href="javascript:void(0)" class="x-next ml-1">次へ</a>
    <a href="javascript:void(0)" class="x-last ml-1 mr-1">最後</a>
    [<span class="x-page"></span>/<span class="x-total"></span>]
`;

export class Nav {
    constructor(ele) {
        this.ele = ele;
        this.ele.innerHTML = tmpl;
        this.map = parseElement(this.ele);
        this.currentPage = 0;
        this.totalPages = 0;
        this.triggerFun = page => {};
        this.map.first.addEventListener("click", event => this.gotoPage(0));
        this.map.prev.addEventListener("click", event => this.gotoPage(this.currentPage - 1));
        this.map.next.addEventListener("click", event => this.gotoPage(this.currentPage + 1));
        this.map.last.addEventListener("click", event => this.gotoPage(this.totalPages - 1));
    }

    adaptToPage(currentPage, totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.map.page.innerText = currentPage + 1;
        this.map.total.innerText = totalPages;
    }

    setTriggerFun(f){
        this.triggerFun = f;
    }

    gotoPage(page) {
        if (page >= 0 && page < this.totalPages && page !== this.currentPage) {
            this.triggerFun(page);
        }
    }
}
