import {createElementFrom} from "./create-element-from.js";
import {parseElement} from "./parse-node.js";

let tmpl = `
<div class="border rounded p-2 mb-2">
    <div class="h5 x-title">タイトル</div>
    <div class="x-body mb-2">Body</div>
    <div class="text-right x-footer"></div>
</div>
`;

export class Widget {
    constructor(){
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
    }

    setTitle(title){
        this.map.title.innerText = title;
    }

    setBody(body){
        if( typeof body === "string" ){
            this.map.body.innerHTML = body;
        } else {
            this.map.body.append(body);
        }
        return parseElement(this.map.body);
    }

    setFooter(footer){
        if( typeof footer === "string" ){
            this.map.footer.innerHTML = footer;
        } else {
            this.map.footer.append(footer);
        }
        return parseElement(this.map.footer);
    }

    close(){
        this.ele.remove();
    }

}