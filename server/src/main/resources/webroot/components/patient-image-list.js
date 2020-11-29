import {Widget} from "../js/widget.js";
import {createElementFrom} from "../js/create-element-from.js";
import {createImageFromBlob} from "../js/createImageFromBlob.js";

let bodyTmpl = `
    <select class="form-control x-select"></select>
    <div>
        <div class="x-preview"></div>
    </div>
`;

let itemTmpl = `
<option></option>
`;

export class PatientImageList extends Widget {
    #rest;
    #patientId = 0;
    #scale = 4.0;

    constructor(rest){
        super();
        this.#rest = rest;
        this.setTitle("画像一覧");
        this.bmap = this.setBody(bodyTmpl);
        this.bmap.select.addEventListener("change", async event => await this.doSelect());
    }

    async init(patientId){
        this.#patientId = patientId;
        let list = await this.#rest.listPatientImage(patientId);
        if( list.length > 10 ){
            this.bmap.select.setAttribute("size", 10);
        }
        for(let file of list){
            let opt = createElementFrom(itemTmpl);
            opt.innerText = file;
            this.bmap.select.append(opt);
        }
    }

    async doSelect(){
        let value = this.bmap.select.querySelector("option:checked").value;
        let blob = await this.#rest.getPatientImageBlob(this.#patientId, value);
        let img = createImageFromBlob(blob);
        img.width = 210 * this.#scale;
        img.height = 297 * this.#scale;
        this.bmap.preview.innerHTML = "";
        this.bmap.preview.append(img);
    }

}