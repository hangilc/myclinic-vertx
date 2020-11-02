import {parseElement} from "./parse-node.js";

let tmpl = `
    <span class="x-setting-name"></span>
`;

export class SettingItem {
    constructor(ele, name){
        if( !ele ){
            ele = document.createElement("div");
        }
        ele.innerHTML = tmpl;
        this.ele = ele;
        let map = parseElement(ele);
        map.settingName.innerText = name;
    }
}