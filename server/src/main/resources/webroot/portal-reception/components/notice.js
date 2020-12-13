import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";

let tmpl = `
<div class="mb-2 border border-info rounded p-2">
    <span class="x-message" style="vertical-align:middle"></span>
    <a href="javascript:void(0)" class="float-right x-close">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" 
            class="bi bi-x" viewBox="0 0 16 16">
            <path fill-rule="evenodd" d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
        </svg>
    </a>
</div>
`;

export class Notice {
    constructor(message){
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.map.message.innerText = message;
        this.map.close.addEventListener("click", event => this.ele.remove());
    }

    autoClose(secs){
        setTimeout(() => {
            this.ele.remove();
        }, secs * 1000);
    }
}