import {parseElement} from "./parse-node.js";
import {PrintAPI} from "./print-api.js";
import {showSettingDetailDialog} from "./setting-detail-dialog.js";

let tmpl = `
    <span class="x-setting-name"></span> ： 
    <a class="x-detail" href="javascript:void(0)">詳細</a> | 
    <a class="x-edit-printer" href="javascript:void(0)">プリンターの変更</a> | 
    <a class="x-edit-aux" href="javascript:void(0)">移動・縮小の変更</a> | 
    <a class="x-delete" href="javascript:void(0)">削除</a>
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
        map.detail.addEventListener("click", async event => await this.doDetail(name));
        map.editPrinter.addEventListener("click", async event => await this.doEditPrinter(name));
    }

    async doDetail(name){
        let api = new PrintAPI();
        let detail = await api.getSettingDetail(name);
        await showSettingDetailDialog(name, detail);
    }

    async doEditPrinter(name){
        let api = new PrintAPI();
        let result = await api.printDialog(name);
        console.log(result);
    }
}