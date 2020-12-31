import {createElementFrom} from "./create-element-from.js";
import {parseElement} from "./parse-node.js";

let tmpl = `
    <div class="modal-dialog-content">
        <div class="d-flex title-wrapper justify-content-between">
            <h3 class="d-inline-block x-title">タイトル</h3>
            <a href="javascript:void(0)" style="font-size: 1.2rem" class="align-item-center x-close-link">&times;</a>
        </div>
        <div class="x-body"></div>
        <div class="command-box x-footer"></div>
    </div>
`;

export class Dialog {
    constructor(opt=null) {
        if( opt === null ){
            opt = {};
        }
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.closeLink.addEventListener("click", event => this.close(undefined));
        if( opt.width ){
            this.ele.style.width = opt.width;
        }
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

    close(resultValue){
        this.ele.dispatchEvent(new CustomEvent("close-dialog", {detail: resultValue}));
    }

    open(onReady){
        let backdrop = document.createElement("div");
        backdrop.classList.add("modal-dialog-backdrop");
        document.body.append(backdrop);
        return new Promise(resolve => {
            this.ele.addEventListener("close-dialog", event => {
                let retVal = event.detail;
                this.ele.remove();
                backdrop.remove();
                resolve(retVal);
            });
            document.body.append(this.ele);
            if( onReady ){
                onReady();
            }
        });
    }

}