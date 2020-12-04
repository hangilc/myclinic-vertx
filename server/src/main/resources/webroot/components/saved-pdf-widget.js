import {Widget} from "../js/widget.js";

let bodyTmpl = `
    <span class="x-path"></span>
    <button type="button" class="btn btn-secondary mr-2 x-display">表示</button>
    <button type="button" class="btn btn-secondary mr-2 d-none x-stamp">捺印</button>
    <button type="button" class="btn btn-secondary mr-2 d-none x-save">保存</button>
    <button type="button" class="btn btn-secondary mr-2 d-none x-delete">削除</button>
    <button type="button" class="btn btn-secondary x-close">閉じる</button>
`;

export class SavedPdfWidget extends Widget {
    constructor(imageUrl) {
        super();
        this.imageUrl = imageUrl;
        this.bmap = this.setBody(bodyTmpl);
        this.bmap.display.addEventListener("click", event => this.doDisplay());
        this.bmap.close.addEventListener("click", event => {
            this.ele.dispatchEvent(new Event("close"));
            this.close();
        });
    }

    async doDisplay(){
        window.open(this.imageUrl, "_blank");
    }

    enableStamp(fun){
        this.bmap.stamp.classList.remove("d-none");
        this.bmap.stamp.addEventListener("click", async event => await fun());
    }

    enableSave(fun){
        this.bmap.save.classList.remove("d-none");
        this.bmap.save.addEventListener("click", async event => await fun());
    }

    setImageUrl(url){
        this.imageUrl = url;
    }

    getImageUrl(){
        return this.imageUrl;
    }
}