import {Component} from "../component.js";
import {Dialog} from "../../../js/dialog2.js";
import {parseElement} from "../../../js/parse-node.js";
import {createElementFrom} from "../../../js/create-element-from.js";
import {gensymId} from "../../../js/gensym-id.js";

let bodyTmpl = `
    <div class="row">
        <div class="col-sm-6 x-left">LEFT</div>
        <div class="col-sm-6 x-right">RIGHT</div>
        <div class="col-sm-12 form-inline x-bottom">BOTTOM</div>
    </div>
`;

let footerTmpl = `
    <button type="button" class="btn btn-primary x-enter">入力</button>
    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
`;

let leftNames = [
    "初診", "再診", "外来管理加算", "特定疾患管理", "尿便検査判断料", "血液検査判断料", "生化Ⅰ判断料",
    "生化Ⅱ判断料", "免疫検査判断料", "微生物検査判断料", "静脈採血"
];

export class ShinryouRegularDialog extends Dialog {
    constructor() {
        super();
        this.setTitle("診療行為入力");
        this.getBody().append(createElementFrom(bodyTmpl));
        this.bmap = parseElement(this.getBody());
        this.addItems()
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        fmap.cancel.addEventListener("click", event => this.close(null));
    }

    addItems(){
        leftNames.forEach(name => {
            let check = createCheck(name);
            this.bmap.left.append(check);
        });
        gensymId(this.getBody());
    }
}

let checkTmpl = `
    <div class="form-check">
        <input type="checkbox" class="form-check-input" />
        <label class="form-check-label"></label>
    </div>
`;

let checkIndex = 1;

function createCheck(label, value){
    let id = `gensym-check-${checkIndex}`
    checkIndex += 1;
    if( !value ){
        value = label;
    }
    let e = createElementFrom(checkTmpl);
    let input = e.querySelector("input");
    input.id = id;
    input.value = value;
    let labelElement = e.querySelector("label");
    labelElement.htmlFor = id;
    labelElement.innerText = label;
    return e;
}

class ShinryouRegularDialogOrig extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.checksElement = map.checks;
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
        this.result = {
            mode: "cancel",
            shinryouIds: null
        };
        this.resolver = result => { console.log("dummy resolver", result)};
    }

    init(visitId) {
        this.enterElement.on("click", async event => {
            let values = [];
            this.checksElement.find("input[type=checkbox]:checked")
                .toArray()
                .forEach(e => {
                    values.push($(e).val());
                });
            let batchEnterResult = await this.rest.batchEnterShinryouByNames(values, visitId);
            this.result.mode = "entered";
            this.result.shinryouIds = batchEnterResult.shinryouIds;
            this.result.drugIds = batchEnterResult.drugIds;
            this.result.conductIds = batchEnterResult.conductIds;
            this.ele.modal("hide");
        });
        this.cancelElement.on("click", event => this.ele.modal("hide"));
        this.ele.on("hidden.bs.modal", event => {
            this.resolver(this.result);
        });
    }

    async open() {
        this.checksElement.find("input[type=checkbox]:checked").prop("checked", false);
        return new Promise(resolve => {
            this.resolver = resolve;
            this.ele.modal("show");
        });
    }
}