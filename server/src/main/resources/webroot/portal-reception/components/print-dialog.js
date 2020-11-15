import {Dialog} from "./dialog.js";
import {parseElement} from "../js/parse-node.js";
import {PrintAPI} from "../../js/print-api.js";
import {DrawerDisp} from "./drawer-disp.js";

let tmpl = `
    <div>
        <div class="x-preview d-flex justify-content-center mb-2"></div>
        <div class="form-inline">
            設定：<select class="x-setting-select form-control mr-2"><option value="--manual--">手動</option></select>
            <a href="http://127.0.0.1:48080/" target="_blank" class="x-open-print-manager">管理画面表示</a>
        </div>
    </div>
`;

let commandsTmpl = `
    <button class="x-print btn btn-primary">印刷</button>
    <button class="x-cancel btn btn-secondary">キャンセル</button>
`;

export class PrintDialog extends Dialog {
    constructor(setup, pages, previewSpec = null){
        super();
        this.setup = setup || [];
        this.pages = pages || [];
        this.api = new PrintAPI("http://127.0.0.1:48080");
        this.getBody().innerHTML = tmpl;
        let map = this.map = parseElement(this.getBody());
        if( previewSpec ){
            let ops = getPageOps(setup, pages, 0);
            let preview = new DrawerDisp(ops, previewSpec.width, previewSpec.height, previewSpec.viewBox);
            map.preview.appendChild(preview.ele);
        }
        this.getFooter().innerHTML = commandsTmpl;
        let cmap = parseElement(this.getFooter());
        cmap.print.addEventListener("click", async event => await this.doPrint());
        cmap.cancel.addEventListener("click", event => this.close());
    }

    async initSetting(kind){
        this.kind = kind;
        let settings = await this.api.listSetting();
        let pref = await this.api.getPref(kind);
        for(let setting of settings){
            let opt = document.createElement("option");
            opt.innerText = setting;
            if( pref && pref === setting ){
                opt.selected = true;
            }
            this.map.settingSelect.appendChild(opt);
        }
    }

    async doPrint(){
        let setting = this.map.settingSelect.value;
        if( setting === "--manual--" ){
            await this.api.print(this.setup, this.pages);
            await this.api.deletePref(this.kind);
            this.close(true);
        } else if( setting ){
            await this.api.print(this.setup, this.pages, setting);
            await this.api.setPref(this.kind, setting);
            this.close(true);
        } else {
            alert("Invalid settting: " + setting);
        }
    }
}

function getPageOps(setup, pages, ipage){
    if( setup && pages && pages.length > 0 ){
        return setup.concat(pages[ipage]);
    } else {
        return [];
    }
}

async function openPrintDialog(docName, setupOps, pagesOps, prog, kind){
    let ele = document.createElement("div");
    ele.innerHTML = tmpl;
    let map = parseElement(ele);
    map.doc.innerText = docName;
    let api = new PrintAPI("http://127.0.0.1:48080");
    let pref = await api.getPref(prog, kind);
    let settings = await api.listSetting();
    for(let setting of settings){
        let opt = document.createElement("option");
        opt.innerText = setting;
        if( pref && pref === setting ){
            opt.selected = true;
        }
        map.select.appendChild(opt);
    }
    return modalOpen(ele, close => {
        map.print.addEventListener("click", async event => {
            let setting = map.select.value;
            if( setting === "--manual--" ){
                await api.print(setupOps, pagesOps);
                await api.deletePref(prog, kind);
                close(true);
            } else if( setting ){
                await api.print(setupOps, pagesOps, setting);
                await api.setPref(prog, kind, setting);
                close(true);
            } else {
                alert("Invalid settting: " + setting);
            }
        });
        map.cancel.addEventListener("click", event => close(false));
    });
}
