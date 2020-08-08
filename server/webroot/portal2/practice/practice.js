import {parseElement} from "../js/parse-element.js";
import {createDropdown} from "../comp/dropdown.js";

let tmpl = `
<h2>診察</h2>
<div>
    <a href="javascript:void(0)" class="x-choose-patient">患者選択▼</a>
</div>
`;

export function createPractice(rest){
    let ele = document.createElement("div");
    ele.innerHTML = tmpl;
    let map = parseElement(ele);
    createDropdown(map.choosePatient, [
        {
            label: "受付患者選択",
            action: () => {}
        },
        {
            label: "患者検索",
            action: () => {}
        },
        {
            label: "最近の診察",
            action: () => {}
        },
        {
            label: "本日の診察",
            action: () => {}
        },
        {
            label: "以前の診察",
            action: () => {}
        }
    ]);
    return ele;
}