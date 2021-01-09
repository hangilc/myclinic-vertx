import {parseElement} from "../../js/parse-node.js";

let tmpl = `
    <div class="border" style="vertical-align:middle; line-height:2em">
        <a href="javascript:void(0)" class="x-first">最初</a>
        <a href="javascript:void(0)" class="x-prev ml-1">前へ</a>
        <a href="javascript:void(0)" class="x-next ml-1">次へ</a>
        [<span class="x-page"></span>]
    </div>
`;

export class NoEndNav {
    constructor(ele) {
        this.ele = ele;
        this.ele.innerHTML = tmpl;
        this.map = parseElement(this.ele);
        this.currentPage = 0;
        this.triggerFun = page => {};
        this.map.first.addEventListener("click", event => this.gotoPage(0));
        this.map.prev.addEventListener("click", event => this.gotoPage(this.currentPage - 1));
        this.map.next.addEventListener("click", event => this.gotoPage(this.currentPage + 1));
    }

    adaptToPage(currentPage) {
        this.currentPage = currentPage;
        this.map.page.innerText = currentPage + 1;
    }

    setTriggerFun(f){
        this.triggerFun = f;
    }

    gotoPage(page) {
        if (page >= 0) {
            this.triggerFun(page);
        }
    }
}

