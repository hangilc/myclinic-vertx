import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";

let previewTmpl = `
<div class="d-flex align-items-start">
    <div class="x-image-wrapper d-inline-block p-2 border border-info rounded mr-2"></div>
    <button class="btn btn-secondary btn-sm x-close">閉じる</button>
</div>
`;

export class PreviewBox {
    constructor(buf){
        this.ele = createElementFrom(previewTmpl);
        let map = parseElement(this.ele);
        let img = document.createElement("img");
        img.src = URL.createObjectURL(new Blob([buf], {type: "image/jpeg"}));
        let scale = 1.8;
        img.width = 210 * scale;
        img.height = 297 * scale;
        map.imageWrapper.append(img);
        map.close.addEventListener("click", event => this.ele.remove());
    }
}

