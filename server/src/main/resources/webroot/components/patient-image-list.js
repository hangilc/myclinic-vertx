import {Widget} from "../js/widget.js";
import {createElementFrom} from "../js/create-element-from.js";
import {createImageFromBlob} from "../js/createImageFromBlob.js";
import {parseElement} from "../js/parse-node.js";

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

export class PatientImageList extends Widget {
    #rest;
    #patientId = 0;

    constructor(rest) {
        super();
        this.#rest = rest;
        this.setTitle("画像一覧");
        this.bmap = this.setBody(bodyTmpl);
        this.bmap.select.addEventListener("change", async event => await this.doSelect());
    }

    async init(patientId) {
        this.#patientId = patientId;
        let list = await this.#rest.listPatientImage(patientId);
        if (list.length > 10) {
            this.bmap.select.setAttribute("size", 10);
        }
        for (let file of list) {
            let opt = createElementFrom(itemTmpl);
            opt.innerText = file;
            this.bmap.select.append(opt);
        }
    }

    async doSelect() {
        let wrapper = this.bmap.previewWrapper;
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
                    if( m.preview.scrollHeight > m.preview.clientHeight ||
                        m.preview.scrollWidth > m.preview.clientWidth ){
                        startX = event.clientX;
                        startY = event.clientY;
                        startScrollLeft = m.preview.scrollLeft;
                        startScrollTop = m.preview.scrollTop;
                    }
                });
                m.preview.addEventListener("mousemove", event => {
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
        }
    }

}