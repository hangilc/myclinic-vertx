import {parseElement} from "../js/parse-node.js";
import {DateInput} from "./date-input.js";
import {RadioGroup} from "./radio-group.js"

let tmpl = `
    <div class="mt-4">
        <form>
            <div class="form-group row">
                <div class="col-sm-2 col-form-label d-flex justify-content-end">保険者番号</div>
                <div class="col-sm-10 form-inline">
                    <input type="text" class="form-control x-hokensha-bangou"/>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 col-form-label d-flex justify-content-end">被保険者</div>
                <div class="col-sm-10 form-inline">
                    記号：<input type="text" class="form-control x-hihokensha-kigou mr-3"/>
                    番号：<input type="text" class="form-control x-hihokensha-bangou"/>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 col-form-label d-flex justify-content-end">本人・家族</div>
                <div class="col-sm-10 form-inline">
                    <div class="form-check form-check-inline">
                        <input type="radio" name="honnin" class="form-check-input" value="1" checked/>
                        <div class="form-check-label">本人</div>
                    </div>
                    <div class="form-check form-check-inline">
                        <input type="radio" name="honnin" class="form-check-input" value="0"/>
                        <div class="form-check-label">家族</div>
                    </div>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 col-form-label d-flex justify-content-end">開始日</div>
                <div class="col-sm-10 form-inline x-valid-from"></div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 col-form-label d-flex justify-content-end">終了日</div>
                <div class="col-sm-10 form-inline x-valid-upto"></div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 col-form-label d-flex justify-content-end">高齢</div>
                <div class="col-sm-10 form-inline">
                    <div class="form-check form-check-inline">
                        <input type="radio" class="form-check-input" checked name="kourei" value="0">
                        <div class="form-check-label">高齢でない</div>
                    </div>
                    <div class="form-check form-check-inline">
                        <input type="radio" class="form-check-input" name="kourei" value="1"/>
                        <div class="form-check-label">１割</div>
                    </div>
                    <div class="form-check form-check-inline">
                        <input type="radio" class="form-check-input" name="kourei" value="2"/>
                        <div class="form-check-label">２割</div>
                    </div>
                    <div class="form-check form-check-inline">
                        <input type="radio" class="form-check-input" name="kourei" value="3"/>
                        <div class="form-check-label">３割</div>
                    </div>
                </div>
            </div>
        </form>
    </div>
`;

export class ShahokokuhoForm {
    constructor(ele) {
        if (!ele) {
            let wrap = document.createElement("div");
            wrap.innerHTML = tmpl;
            ele = wrap.firstChild;
        }
        if (ele.children && ele.children.length === 0) {
            ele.innerHTML = tmpl;
        }
        this.ele = ele;
        let map = parseElement(ele);
        this.error = null;
        this.hokenshaBangouElement = map.hokenshaBangou;
        this.hihokenshaKigouElement = map.hihokenshaKigou;
        this.hihokenshaBangouElement = map.hihokenshaBangou;
        this.honninElement = new RadioGroup(ele.querySelector("form"), "honnin");
        this.validFromElement = new DateInput(map.validFrom);
        this.validUptoElement = new DateInput(map.validUpto);
        [this.validFromElement, this.validUptoElement].forEach(e => e.setGengouList(
            "令和",
            [
                "平成", "令和"
            ]));
        this.validUptoElement.allowEmpty();
        this.koureiElement = new RadioGroup(ele.querySelector("form"), "kourei");
    }

    set(shahokokuho) {
        if (shahokokuho) {
            this.hokenshaBangouElement.value = shahokokuho.hokenshaBangou;
            this.hihokenshaKigouElement.value = shahokokuho.hihokenshaKigou;
            this.hihokenshaBangouElement.value = shahokokuho.hihokenshaBangou;
            this.honninElement.set(shahokokuho.honnin);
            this.validFromElement.set(shahokokuho.validFrom);
            this.validUptoElement.set(shahokokuho.validUpto);
            this.koureiElement.set(shahokokuho.kourei);
        } else {
            this.hokenshaBangouElement.value = null;
            this.hihokenshaKigouElement.value = null;
            this.hihokenshaBangouElement.value = null;
            this.honninElement.value = 0;
            this.validFromElement.clear();
            this.validUptoElement.clear();
            this.koureiElement.set(0);
        }
        return this;
    }

    getError() {
        let err = this.error;
        this.error = null;
        return err;
    }

    clearValidUpto() {
        this.validUptoElement.val(null);
    }

    get(shahokokuhoId, patientId) {
        let hokenshaBangouInput = this.hokenshaBangouElement.value;
        if (hokenshaBangouInput === "") {
            this.error = "保険者番号が入力されていません。";
            return undefined;
        }
        let hokenshaBangou = parseInt(hokenshaBangouInput);
        if (isNaN(hokenshaBangou)) {
            this.error = "保険者番号の入力が不適切です。";
            return undefined;
        }
        let hihokenshaKigou = this.hihokenshaKigouElement.value;
        let hihokenshaBangou = this.hihokenshaBangouElement.value;
        if (hihokenshaKigou === "" && hihokenshaBangou === "") {
            this.error = "被保険者情報が入力されていません。";
            return undefined;
        }
        let honninInput = this.honninElement.get();
        let honnin = parseInt(honninInput);
        if (!(honnin === 1 || honnin === 0)) {
            this.error = "本人・家族の入力が不適切です。";
            return undefined;
        }
        let validFrom = this.validFromElement.get();
        if (!validFrom) {
            this.error = "開始日の入力が不適切です。";
            return undefined;
        }
        let validUpto = this.validUptoElement.get();
        if (!validUpto) {
            this.error = "終了日の入力が不適切です。";
            return undefined;
        }
        let koureiInput = this.koureiElement.get();
        let kourei = parseInt(koureiInput);
        if (!(kourei >= 0 && kourei <= 3)) {
            this.error = "高齢の入力が不適切です。";
            return undefined;
        }
        return {
            shahokokuhoId,
            patientId,
            hokenshaBangou,
            hihokenshaKigou,
            hihokenshaBangou,
            honnin,
            validFrom,
            validUpto,
            kourei
        };
    }
}