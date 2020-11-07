import {gensymId} from "../js/gensym-id.js";

let tmpl = `
<div class="form-row">
    <div class="form-group col-auto">
        <label for="gensym-last-name">姓</label>
        <input id="gensym-last-name" class="form-control"/>
    </div>
    <div class="form-group col-auto">
        <label for="gensym-first-name">名</label>
        <input id="gensym-first-name" class="form-control"/>
    </div>
</div>
<div class="form-row">
    <div class="form-group col-auto">
        <label for="gensym-last-name">姓（よみ）</label>
        <input id="gensym-last-name-yomi" class="form-control"/>
    </div>
    <div class="form-group col-auto">
        <label for="gensym-first-name">名（よみ）</label>
        <input id="gensym-first-name-yomi" class="form-control"/>
    </div>
</div>
`;

export class PatientForm {
    constructor(ele){
        if( !ele ){
            ele = document.createElement("div");
        }
        ele.innerHTML = tmpl;
        gensymId(ele);
        this.ele = ele;
    }
}