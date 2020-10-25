import {parseElement} from "../js/parse-node.js";

let tmpl = `
    <div class="d-flex p-2" style="background-color: #ccc;">
        <div class="font-weight-bold flex-grow-1 x-title"></div>
        <div><span class="font-weight-bold x-close"
                   style="cursor: pointer;">Ｘ</span></div>
    </div>
    <div class="x-content mt-4"></div>
`;

let symTitle = Symbol("title");
let symClosing = Symbol("closing");
let symContent = Symbol("content");

export class Widget {
    constructor(){
        let ele = document.createElement("div");
        ele.classList.add("mb-3", "border", "border-secondary", "rounded", "p-3");
        ele.innerHTML = tmpl;
        let map = parseElement(ele);
        map.close.addEventListener("click", event => this.close());
        this[symTitle] = map.title;
        this[symContent] = map.content;
        this.ele = ele;
    }

    close(){
        let cb = this[symClosing];
        if( cb && cb() === false ){
            return;
        }
        this.ele.parentNode.removeChild(this.ele);
        this.ele.dispatchEvent(new Event("widget-closed"));
    }

    setTitle(title){
        this[symTitle].innerText = title;
    }

    getContentElement(){
        return this[symContent];
    }

    onClosing(cb){
        this[symClosing] = cb;
    }

    onClosed(cb){
        this.ele.addEventListener("widget-closed", event => cb());
    }

}