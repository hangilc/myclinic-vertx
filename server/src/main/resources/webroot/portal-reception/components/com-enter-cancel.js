import {parseElement} from "../js/parse-node.js";

let tmpl = `
<button class="x-enter btn btn-primary">入力</button>
<button class="x-cancel btn btn-secondary">キャンセル</button>
`;

export class ComEnterCancel {
    static populate(ele){
        ele.innerHTML = tmpl;
        return parseElement(ele);
    }
}