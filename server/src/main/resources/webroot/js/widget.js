import {createElementFrom} from "./create-element-from.js";
import {parseElement} from "./parse-node.js";
import {click} from "./dom-helper.js";

let tmpl = `
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2 mb-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1 x-title"></div>
            <div><span class="font-weight-bold x-close"
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
        click(this.map.close, event => this.close(undefined));
    }

    setTitle(title){
        this.map.title.innerText = title;
    }

    getBody(){
        return this.map.body;
    }

    setBody(src){
        if( typeof src === "string" ){
            this.getBody().innerHTML = src;
        } else {
            this.getBody().append(src);
        }
        return parseElement(this.getBody());
    }

    getFooter(){
        return this.map.footer;
    }

    setFooter(src){
        if( typeof src === "string" ){
            this.getFooter().innerHTML = src;
        } else {
            this.getFooter().append(src);
        }
        return parseElement(this.getFooter());
    }

    close(result){
        this.ele.remove();
        if( this.resolve ){
            this.resolve(result);
        }
    }

    wait(){
        return new Promise(resolve => {
            this.resolve = resolve;
        });
    }
}