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
        if( total > 0 ){
            this.ele.removeClass("d-none");
        } else {
            this.ele.addClass("d-none");
        }
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
        this.on("change", (event, page) => cb(event, page));
    }

}