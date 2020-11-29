import {Widget} from "../js/widget.js";
import {createElementFrom} from "../js/create-element-from.js";
import {createImageFromBlob} from "../js/createImageFromBlob.js";
import {parseElement} from "../js/parse-node.js";
import * as kanjidate from "../js/kanjidate.js";
import {ChangePatientImageDialog} from "./change-patient-image-dialog.js";

let defaultScale = 4.0;
let defaultImageWidth = 210 * defaultScale;
let defaultImageHeight = 297 * defaultScale;

let bodyTmpl = `
    <select class="form-control mb-2 x-select"></select>
    <div class="mb-2 x-preview-wrapper"></div>
`;

let imgPreviewTmpl = `
    <div class="mb-2 x-preview" style="overflow: auto; 
        max-height: ${defaultImageHeight}px"></div>
    <div>
        <button class="btn btn-secondary btn-sm x-larger">拡大</button>
        <button class="btn btn-secondary btn-sm x-smaller">縮小</button>
    </div>
`;

let pdfPreviewTmpl = `
    <div class="mb-2 x-preview">PDF ファイル</div>
    <div>
        <button class="btn btn-secondary btn-sm x-open-in-window">別ウィンドウで開く</button>
    </div>
`;

let itemTmpl = `
<option></option>
`;

let footerTmpl = `
    <button class="btn btn-secondary d-none x-change-patient">患者変更</button>
    <button class="btn btn-secondary x-close">閉じる</button>
`;

let dateRegex = /\b(\d{4})(\d{2})(\d{2})/;

class Item {
    constructor(file){
        this.file = file;
        let m = file.match(dateRegex);
        if( m ){
            console.log(m);
            this.date = m[0];
            this.year = parseInt(m[1]);
            this.month = parseInt(m[2]);
            this.day = parseInt(m[3]);
        } else {
            this.date = "0000-00-00";
        }
    }

    getDateRep(){
        if( this.year ){
            let [g, n] = kanjidate.seirekiToGengou(this.year, this.month, this.day);
            return `${g}${n}年${this.month}月${this.day}日`;
        } else {
            return "";
        }
    }
}

export class PatientImageList extends Widget {
    #rest;
    #isAdmin;
    #patientId = 0;
    #file = null;

    constructor(rest, isAdmin = false) {
        super();
        this.#rest = rest;
        this.#isAdmin = isAdmin;
        this.setTitle("画像一覧");
        this.bmap = this.setBody(bodyTmpl);
        this.bmap.select.addEventListener("change", async event => await this.doSelect());
        this.cmap = this.setFooter(footerTmpl);
        this.cmap.close.addEventListener("click", event => this.close());
        if( this.#isAdmin ){
            this.cmap.changePatient.classList.remove("d-none");
            this.cmap.changePatient.addEventListener("click", async event => await this.doChangePatient());
        }
    }

    async init(patientId) {
        this.#patientId = patientId;
        let list = await this.#rest.listPatientImage(patientId);
        if (list.length > 10) {
            this.bmap.select.setAttribute("size", 10);
        }
        let items = list.map(file => new Item(file));
        items.sort((a, b) => {
            let da = a.date;
            let db = b.date;
            if( da < db ){
                return -1;
            } else if( da > db ){
                return 1;
            } else {
                return 0;
            }
        });
        items.reverse();
        for(let item of items){
            let date = item.getDateRep();
            let datePart = date ? ` (${date})` : "";
            let opt = createElementFrom(itemTmpl);
            opt.innerText = `${item.file}${datePart}`;
            opt.value = item.file;
            this.bmap.select.append(opt);
        }
    }

    async doSelect() {
        let wrapper = this.bmap.previewWrapper;
        wrapper.innerHTML = "";
        this.#file = null;
        let file = this.bmap.select.querySelector("option:checked").value;
        if (file) {
            if( file.endsWith(".pdf") ){
                wrapper.innerHTML = pdfPreviewTmpl;
                let m = parseElement(wrapper);
                m.openInWindow.addEventListener("click", event => {
                    let url = this.#rest.urlOfPatientImage(this.#patientId, file);
                    window.open(url, "_blank");
                });
            } else {
                let blob = await this.#rest.getPatientImageBlob(this.#patientId, file);
                let img = createImageFromBlob(blob);
                img.width = defaultImageWidth;
                img.height = defaultImageHeight;
                wrapper.innerHTML = imgPreviewTmpl;
                let m = parseElement(wrapper);
                m.preview.append(img);
                m.larger.addEventListener("click", event => {
                    img.width = img.width * 1.5;
                    img.height = img.height * 1.5;
                });
                m.smaller.addEventListener("click", event => {
                    img.width = img.width / 1.5;
                    img.height = img.height / 1.5;
                });
                let startX = null;
                let startY = null;
                let startScrollLeft = null;
                let startScrollTop = null;
                m.preview.addEventListener("mousedown", event => {
                    if( event.buttons !== 1 ){ // if it's not left button click
                        return;
                    }
                    if( m.preview.scrollHeight > m.preview.clientHeight ||
                        m.preview.scrollWidth > m.preview.clientWidth ){
                        startX = event.clientX;
                        startY = event.clientY;
                        startScrollLeft = m.preview.scrollLeft;
                        startScrollTop = m.preview.scrollTop;
                    }
                });
                m.preview.addEventListener("mousemove", event => {
                    if( event.buttons !== 1 ){
                        return;
                    }
                    if( m.preview.scrollHeight > m.preview.clientHeight && startX != null ){
                        event.preventDefault();
                        let dx = event.clientX - startX;
                        let dy = event.clientY - startY;
                        m.preview.scrollLeft = startScrollLeft - dx;
                        m.preview.scrollTop = startScrollTop - dy;
                    }
                });
                m.preview.addEventListener("mouseup", event => {
                    startX = null;
                    startY = null;
                });
            }
            this.#file = file;
        }
    }

    async doChangePatient(){
        if( this.#patientId > 0 && this.#file ){
            let dialog = new ChangePatientImageDialog(this.#patientId, this.#file, this.#rest);
            await dialog.open();
        }
    }

}