import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";

let tmpl = `
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2 mb-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1 x-title"></div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="x-body"></div>
        <div class="text-right mt-2  x-footer"></div>
    </div>
`;

export class Widget {
    constructor() {
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
    }

    setTitle(title){
        this.map.title.innerText = title;
    }

    getBody(){
        return this.map.body;
    }

    getFooter(){
        return this.map.footer;
    }

    close(result){
        this.ele.remove();
        this.resolve(result);
    }

    open(wrapper){
        return new Promise(resolve => {
            this.resolve = resolve;
            wrapper.
        });
    }
}