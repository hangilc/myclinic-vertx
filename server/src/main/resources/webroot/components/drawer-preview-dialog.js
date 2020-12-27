import {Dialog} from "../js/dialog2.js";
import {drawerToSvg} from "../js/drawer-svg.js";
import {parseElement} from "../js/parse-node.js";

let footerTmpl = `
    <div class="form-inline d-inline-block mr-2">
        設定：<select class="x-setting-select form-control mr-2"><option value="--manual--">手動</option></select>
        <a href="http://127.0.0.1:48080/" target="_blank" class="x-open-print-manager">管理画面表示</a>
    </div>
    <button type="button" class="btn btn-primary d-none x-print">印刷</button>
    <button type="button" class="btn btn-secondary x-close">閉じる</button>
`;

function resolveDimension(pageSize){
    if( pageSize.endsWith("_Landscape") ){
        return ["210mm", "148mm"];
    } else {
        return ["148mm", "210mm"];
    }
}

function resolveViewBox(paperSize){
    switch(paperSize){
        case "A5": return "0 0 148, 210";
        case "A6_Landscape": return "0 0 148 105";
        default: return "0 0 210, 297";
    }
}

export class DrawerPreviewDialog extends Dialog {
    constructor(setup, pages, paperSize = "A4") {
        super();
        this.ele.style.maxHeight = (window.innerHeight - 60) + "px";
        this.setup = setup;
        this.pages = pages;
        let opt = {};
        [opt.width, opt.height] = resolveDimension(paperSize);
        opt.viewBox = resolveViewBox(paperSize);
        this.currentOps = this.getPage(0);
        let svg = drawerToSvg(this.currentOps, opt);
        this.getBody().append(svg);
        this.getFooter().innerHTML = footerTmpl;
        let fmap = this.fmap = parseElement(this.getFooter());
        fmap.print.addEventListener("click", async event => await this.doPrint());
        fmap.close.addEventListener("click", event => this.close(false));
    }

    async setPrintAPI(printAPI){
        this.printAPI = printAPI;
        this.fmap.print.classList.remove("d-none");
    }

    async doPrint(){
        await this.printAPI.print([], [this.currentOps], null);
        this.close(true);
    }

    getPage(page){
        if( page >= 0 && page <= this.pages.length ){
            return this.setup.concat(this.pages[page]);
        } else {
            return [];
        }
    }

}