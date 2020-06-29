import {Component} from "../component.js";
import {parseElement} from "../../js/parse-element.js";

let template = `
    <div class="d-none mt-2">
        <a href="javascript:void(0)" class="x-first">最初</a>
        <a href="javascript:void(0)" class="x-prev ml-1">前へ</a>
        <a href="javascript:void(0)" class="x-next ml-1">次へ</a>
        <a href="javascript:void(0)" class="x-last ml-1 mr-1">最後</a>
        [<span class="x-page"></span>/<span class="x-total"></span>]
    </div>
`;

class Nav extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.firstElement = map.first;
        this.prevElement = map.prev;
        this.nextElement = map.next;
        this.lastElement = map.last;
        this.pageElement = map.page;
        this.totalElement = map.total;
    }

    init(){
        this.set(0, 0);
        this.firstElement.on("click", event => this.gotoPage(1, this.getTotalPage()));
        this.prevElement.on("click", event => this.gotoPage(this.getCurrentPage() - 1, this.getTotalPage()));
        this.nextElement.on("click", event => this.gotoPage(this.getCurrentPage() + 1, this.getTotalPage()));
        this.lastElement.on("click", event => this.gotoPage(this.getTotalPage(), this.getTotalPage()));
    }

    set(page, total){
        if( page > total ){
            page = total;
        }
        this.setPage(page);
        this.setTotal(total);
    }

    setPage(page){
        this.pageElement.text(page);
    }

    setTotal(total){
        this.totalElement.text(total);
    }

    gotoPage(page){
        let total = this.getTotalPage();
        if( page >= 1 && page <= total ){
            this.triggerChange(page, total);
        }
    }

    getCurrentPage(){
        return parseInt(this.pageElement.text());
    }

    getTotalPage(){
        return parseInt(this.totalElement.text());
    }

    triggerChange(page){
        this.trigger("change", page);
    }

    onChange(cb){
        this.on("change", (event, page) => cb(page));
    }

    show(){
        this.ele.removeClass("d-none");
    }

    hide(){
        this.ele.addClass("d-none");
    }

}

class NavFactory {
    create(rest){
        let ele = $(template);
        let map = parseElement(ele);
        let comp = new Nav(ele, map, rest);
        comp.init();
        comp.set(0, 0);
        return comp;
    }
}

export let navFactory = new NavFactory();

