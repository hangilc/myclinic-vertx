import {parseElement} from "./parse-node.js";
import {PrintAPI} from "./print-api.js";
import {showSettingDetailDialog} from "./setting-detail-dialog.js";
import {openEditAuxSettingDialog} from "./setting-edit-aux-dialog.js";

let tmpl = `
    <span class="x-setting-name"></span> ： 
    <a class="x-detail" href="javascript:void(0)">詳細</a> | 
    <a class="x-edit-printer" href="javascript:void(0)">プリンターの変更</a> | 
    <a class="x-edit-aux" href="javascript:void(0)">移動・縮小の変更</a> | 
    <a class="x-delete" href="javascript:void(0)">削除</a>
`;

export class SettingItem {
    constructor(ele, name){
        this.api = new PrintAPI();
        if( !ele ){
            ele = document.createElement("div");
        }
        ele.innerHTML = tmpl;
        this.ele = ele;
        let map = parseElement(ele);
        map.settingName.innerText = name;
        map.detail.addEventListener("click", async event => await this.doDetail(name));
        map.editPrinter.addEventListener("click", async event => await this.doEditPrinter(name));
        map.editAux.addEventListener("click", async event => await this.doEditAux(name));
    }

    async doDetail(name){
        let detail = await this.api.getSettingDetail(name);
        await showSettingDetailDialog(name, detail);
    }

    async doEditPrinter(name){
        let result = await this.api.printDialog(name);
        if( result ){
            await this.api.updateSetting(name, result);
        }
    }

    async doEditAux(name){
        let current = await this.api.getSetting(name);
        if( current ){
            let aux = current.auxSetting;
            let result = await openEditAuxSettingDialog(name, aux, this.api);
        }
    }
}