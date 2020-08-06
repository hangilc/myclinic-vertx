import {parseElement} from "../js/parse-element.js";

let tmpl = `
<textarea></textarea>
<div>
    <button type="button" class="btn btn-primary x-create">作成</button>
</div>
`;

export class MedCert {
    constructor(rest) {
        this.rest = rest;
        this.ele = $(tmpl);
        let map = parseElement(this.ele);
        map.create.on("click", event => this.doCreate());
    }

    async doCreate(){
        let data = {};
        let savePath = this.rest.renderMedCert(data);
        alert(savePath);
    }

}