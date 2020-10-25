import {parseElement} from "../js/parse-node.js";

let html = `
    <form class="form-inline x-form" onsubmit="return false">
        <input type="radio" name="sex" value="M"> 男
        <input type="radio" name="sex" value="F" checked class="ml-2"> 女
    </form>
`;

let symForm = Symbol("form");

export class SexInput {
    constructor(ele) {
        if( !ele ){
            ele = document.createElement("div");
        }
        if( ele.children && ele.children.length === 0 ){
            ele.innerHTML = html;
        }
        this.ele = ele;
        let map = parseElement(ele);
        this[symForm] = map.form;
    }

    set(sex){
        let form = this[symForm];
        form.querySelector(`input[name=sex][value=${sex}]`).style.checked = true;
    }

    get(){
        let form = this[symForm];
        return form.querySelector("input[name=sex]:checked").value;
    }

}