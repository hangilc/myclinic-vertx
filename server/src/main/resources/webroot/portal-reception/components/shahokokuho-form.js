import {createElementFrom} from "../js/create-element-from.js";
import {DateInput} from "./date-input.js";
import {parseElement} from "../js/parse-node.js";
import {success, error} from "../js/opt-result.js";
import {getRadioValue, setRadioValue} from "./dom-util.js";

let tmpl = `
<form class="x-form">
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
            番号：<input type="text" class="form-control x-hihokensha-bangou mr-3"/>
            枝番：<input type="text" class="form-control x-hihokensha-edaban" size="4"/>
        </div>
    </div>
    <div class="form-group row">
        <div class="col-sm-2 col-form-label d-flex justify-content-end">本人・家族</div>
        <div class="col-sm-10 form-inline">
            <div class="form-check form-check-inline">
                <input type="radio" name="honnin" class="form-check-input" value="1"/>
                <div class="form-check-label">本人</div>
            </div>
            <div class="form-check form-check-inline">
                <input type="radio" name="honnin" class="form-check-input" value="0" checked/>
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
`;

// noinspection NonAsciiCharacters
export class ShahokokuhoForm {
    constructor(shahokokuho) {
        this.ele = createElementFrom(tmpl);
        this.validFromInput = new DateInput();
        this.validFromInput.setGengouRecent();
        this.validUptoInput = new DateInput();
        this.validUptoInput.setGengouRecent();
        this.validUptoInput.setAllowEmpty(true);
        this.validUptoInput.setEmptyValue("0000-00-00");
        this.map = parseElement(this.ele);
        this.map.validFrom.appendChild(this.validFromInput.ele);
        this.map.validUpto.appendChild(this.validUptoInput.ele);
        this.shahokokuhoId = 0;
        this.patientId = 0;
        if (shahokokuho) {
            this.set(shahokokuho);
        }
    }

    getHonnin() {
        let value = this.map.form.querySelector("input[type='radio'][name='honnin']:checked").value;
        let honnin = parseInt(value);
        return isNaN(honnin) ? error("本人・家族の入力が不適切です。") : success(honnin);
    }

    setHonnin(honnin) {
        setRadioValue(this.map.form, "honnin", honnin);
    }

    getKourei() {
        let value = this.map.form.querySelector("input[type='radio'][name='kourei']:checked").value;
        let kourei = parseInt(value);
        return isNaN(kourei) ? error("高齢の入力が不適切です。") : success(kourei);
    }

    setKourei(kourei) {
        setRadioValue(this.map.form, "kourei", kourei);
    }

    setPatientId(patientId) {
        this.patientId = patientId;
    }

    set(shahokokuho) {
        this.shahokokuhoId = shahokokuho.shahokokuhoId;
        this.patientId = shahokokuho.patientId;
        this.map.hokenshaBangou.value = shahokokuho.hokenshaBangou;
        this.map.hihokenshaKigou.value = shahokokuho.hihokenshaKigou;
        this.map.hihokenshaBangou.value = shahokokuho.hihokenshaBangou;
        this.map.hihokenshaEdaban.value = shahokokuho.edaban;
        this.setHonnin(shahokokuho.honnin);
        this.validFromInput.set(shahokokuho.validFrom);
        this.validUptoInput.set(shahokokuho.validUpto);
        this.setKourei(shahokokuho.kourei);
    }

    get() {
        let shahokokuhoId = this.shahokokuhoId;
        let patientId = this.patientId;
        if (!patientId) {
            return error("患者番号が設定されていません。");
        }
        let hokenshaBangouInput = this.map.hokenshaBangou.value;
        if (hokenshaBangouInput === "") {
            return error("保険者番号が入力されていません。");
        }
        let hokenshaBangou = parseInt(hokenshaBangouInput);
        if (isNaN(hokenshaBangou)) {
            return error("保険者番号の入力が不適切です。");
        }
        let hihokenshaKigou = this.map.hihokenshaKigou.value;
        let hihokenshaBangou = this.map.hihokenshaBangou.value;
        if (hihokenshaKigou === "" && hihokenshaBangou === "") {
            return error("被保険者情報が入力されていません。");
        }
        let edaban = this.toHankaku(this.map.hihokenshaEdaban.value);
        if (edaban.length > 2) {
            return error("枝番が２文字以上です。");
        }

        let honninOpt = this.getHonnin();
        if (!honninOpt.ok) {
            return error(honninOpt.message);
        }
        let honnin = honninOpt.value;
        if (!(honnin === 1 || honnin === 0)) {
            return error("本人・家族の入力が不適切です。");
        }
        let validFromOpt = this.validFromInput.get();
        if (!validFromOpt.ok) {
            return error("開始日：" + validFromOpt.message);
        }
        let validFrom = validFromOpt.value;
        let validUptoOpt = this.validUptoInput.get();
        if (!validUptoOpt.ok) {
            return error("終了日：" + validUptoOpt.message);
        }
        let validUpto = validUptoOpt.value;
        let koureiOpt = this.getKourei();
        if (!koureiOpt.ok) {
            return error("高齢：" + koureiOpt.message);
        }
        let kourei = koureiOpt.value;
        if (!(kourei >= 0 && kourei <= 3)) {
            return error("高齢の入力が不適切です。");
        }
        return success({
                shahokokuhoId,
                patientId,
                hokenshaBangou,
                hihokenshaKigou,
                hihokenshaBangou,
                honnin,
                validFrom,
                validUpto,
                kourei,
                edaban
            });
    }

    toHankaku(s) {
        return this.mapString(s, ch => this.zenkakuToHankaku(ch));
    }

    mapString(s, f) {
        return s.split("").map(ch => f(ch)).join("");
    }

    zenkakuToHankaku(ch) {
        let map = {
            "０": "0",
            "１": "1",
            "２": "2",
            "３": "3",
            "４": "4",
            "５": "5",
            "６": "6",
            "７": "7",
            "８": "8",
            "９": "9",
        }
        return map[ch] || ch
    }

}