import {parseElement} from "../js/parse-element.js";

let tmpl = `
    <div class="workarea">
        <span class="x-path"></span>
        <button type="button" class="x-display">表示</button>
        <button type="button" class="x-stamp">捺印</button>
        <button type="button" class="x-save">保存</button>
        <button type="button" class="x-delete">削除</button>
        <a href="javascript:void(0)" class="x-close">閉じる</a>
    </div>
`;

export class SavedPdfWorkarea {
    constructor(rest, pdfPath, patientId){
        this.rest = rest;
        this.pdfPath = pdfPath;
        this.patientId = patientId;
        this.ele = $(tmpl);
        let map = parseElement(this.ele);
        this.pathElement = map.path.text(truncatePath(pdfPath));
        map.display.on("click", event => this.doDisplay());
        map.stamp.on("click", event => this.doStamp());
        map.save.on("click", event => this.doSave());
        map.close.on("click", event => this.ele.remove());
    }

    doDisplay(){
        let url = this.rest.url("/show-pdf", {file: this.pdfPath});
        window.open(url, "_blank");
    }

    async doStamp(){
        let srcFile = this.pdfPath;
        let stampInfo = await this.rest.referStampInfo();
        let imageFile = stampInfo.imageFile;
        let dstFile = await this.rest.createTempFileName("refer-stamped-", ".pdf");
        await this.rest.putStampOnPdf(srcFile, imageFile, dstFile, {
            scale: stampInfo.scale,
            xPos: stampInfo.xPos,
            yPos: stampInfo.yPos,
            stampCenterRelative: stampInfo.isImageCenterRelative
        });
        let oldFile = this.pdfPath;
        this.pdfPath = dstFile;
        await this.rest.deleteFile(oldFile);
    }

    async doSave(){
        let savePath = await this.getReferSavePath();
        await this.rest.copyFile(this.pdfPath, savePath, true);
        alert("保存されました。" + savePath);
    }

    async getReferSavePath(){
        return await this.rest.createReferImageSavePath(this.patientId, "pdf");
    }

}

function truncatePath(path){
    let s;
    if( path.length <= 10 ){
        s = path;
    } else {
        s = path.substring(0, 10) + "...";
    }
    return s;
}