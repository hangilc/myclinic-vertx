import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";

let tmpl = `
<div class="border rounded mb-3 p-2">
    <div>ファイルのアップロード</div>
    <div class="mt-2 x-items"></div>
    <div class="mt-4">
        <a href="javascript:void(0)" class="x-close ml-2">閉じる</a>
    </div>
</div>
`;

let itemTmpl = `
<div>
    <div class="x-name"></div>
    <div class="x-status">
        <span class="x-status-message"></span>
        <button class="btn btn-danger btn-sm d-none x-retry">再送信</button>
    </div>
</div>
`;

export class UploadProgress {
    constructor(uploads){
        this.ele = createElementFrom(tmpl);
        let map = this.map = parseElement(this.ele);
        this.remaining = uploads.length;
        uploads.forEach(upload => this.addItem(upload));
        this.map.close.addEventListener("click", event => this.doClose());
    }

    addItem(upload){
        let item = new UploadItem(upload);
        item.ele.addEventListener("uploaded", event => this.remaining -= 1);
        this.map.items.append(item.ele);
    }

    doClose(){
        if( this.remaining > 0 ){
            if( !confirm("アップロードが完了していませんが、閉じますか？") ){
                return;
            }
        }
        this.ele.remove();
    }
}

class UploadItem {
    constructor(upload){
        this.ele = createElementFrom(itemTmpl);
        let map = this.map = parseElement(this.ele);
        map.name.innerText = upload.getLabel();
        this.setStatusMessage("アップロード中");
        upload.setProgressHandler(pct => {
            this.setStatusMessage(`${pct}%`);
        });
        let act = upload.upload();
        act.promise.then((msg) => {
            if( act.xhr.status === 200 ){
                this.setStatusMessage("完了");
                this.ele.dispatchEvent(new Event("uploaded"));
            } else {
                this.setErrorStatus(`エラー：${act.xhr.status}`);
            }
        }, (msg) => {
            this.setErrorStatus(msg);
        });
        map.retry.addEventListener("click", event => {
            act.abort();
            let newItem = new UploadItem(upload);
            this.ele.parentElement.replaceChild(newItem.ele, this.ele);
        });
    }

    clearStatusMessageColor(){
        this.map.statusMessage.classList.remove("text-danger");
    }

    setStatusMessage(msg){
        this.clearStatusMessageColor();
        this.map.statusMessage.innerText = msg;
    }

    setErrorStatus(msg){
        this.clearStatusMessageColor();
        this.map.statusMessage.innerText = msg;
        this.map.statusMessage.classList.add("text-danger");
        this.map.retry.classList.remove("d-none");
    }
}