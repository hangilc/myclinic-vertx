import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";

let tmpl = `
<div></div>
`;

let titleTmpl = `
<div></div>
`;

let itemTmpl = `
<div class="row">
    <div class="col-4 text-right x-content"></div>
    <div class="col-8 pl-2 text-right x-account"></div>
</div>
`;

export class MeisaiDisp {
    constructor(meisai){
        this.ele = createElementFrom(tmpl);
        for(let sect of meisai.sections){
            this.ele.appendChild(createTitle(sect.label));
            for(let item of sect.items){
                let itemElement = createElementFrom(itemTmpl);
                let imap = parseElement(itemElement);
                console.log(imap);
                imap.content.innerText = item.label;
                imap.account.innerHTML = `${item.tanka} &times; ${item.count} = ${item.tanka * item.count}`;
                this.ele.appendChild(itemElement);
            }
        }
    }
}

function createTitle(label){
    let ele = createElementFrom(titleTmpl);
    ele.innerText = label;
    return ele;
}