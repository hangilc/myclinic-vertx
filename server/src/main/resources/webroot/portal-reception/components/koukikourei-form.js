import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {success, error} from "../js/opt-result.js";
import {DateInput} from "./date-input.js";
import {getRadioValue, setRadioValue} from "./dom-util.js";

let tmpl = `
<form class="x-form" onsubmit="return false;">
    <div class="form-group row">
        <div class="col-sm-3 col-form-label d-flex justify-content-end">保険者番号</div>
        <div class="col-sm-9 form-inline">
            <input type="text" class="form-control x-hokensha-bangou"/>
        </div>
    </div>
    <div class="form-group row">
        <div class="col-sm-3 col-form-label d-flex justify-content-end">被保険者番号</div>
        <div class="col-sm-9 form-inline">
            <input type="text" class="form-control x-hihokensha-bangou"/>
        </div>
    </div>
    <div class="form-group row">
        <div class="col-sm-3 col-form-label d-flex justify-content-end">開始日</div>
        <div class="col-sm-9 form-inline x-valid-from"></div>
    </div>
    <div class="form-group row">
        <div class="col-sm-3 col-form-label d-flex justify-content-end">終了日</div>
        <div class="col-sm-9 form-inline x-valid-upto"></div>
    </div>
    <div class="form-group row">
        <div class="col-sm-3 col-form-label d-flex justify-content-end">負担割</div>
        <div class="col-sm-9 form-inline">
            <div class="form-check form-check-inline">
                <input type="radio" class="form-check-input" name="futan-wari" value="1" checked/>
                <div class="form-check-label">１割</div>
            </div>
            <div class="form-check form-check-inline">
                <input type="radio" class="form-check-input" name="futan-wari" value="2"/>
                <div class="form-check-label">２割</div>
            </div>
            <div class="form-check form-check-inline">
                <input type="radio" class="form-check-input" name="futan-wari" value="3"/>
                <div class="form-check-label">３割</div>
            </div>
        </div>
    </div>
</form>
`;

export class KoukikoureiForm {
    constructor(koukikourei){
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.validFrom = new DateInput();
        this.validFrom.setGengouRecent();
        this.validUpto = new DateInput();
        this.validUpto.setGengouRecent();
        this.validUpto.setAllowEmpty(true);
        this.validUpto.setEmptyValue("0000-00-00");
        this.map.validFrom.append(this.validFrom.ele);
        this.map.validUpto.append(this.validUpto.ele);
        this.koukikoureiId = 0;
        this.patientId = 0;
        if( koukikourei ){
            this.set(koukikourei);
        }
    }

    setPatientId(patientId){
        this.patientId = patientId;
    }

    set(koukikourei){
        this.koukikoureiId = koukikourei.koukikoureiId;
        this.patientId = koukikourei.patientId;
    }

    get(){
        let koukikoureiId = this.koukikoureiId;
        let patientId = this.patientId;
        if( !patientId ){
            return error("患者番号が設定されていません。");
        }
        let hokenshaBangou = this.map.hokenshaBangou.value;
        if( hokenshaBangou === "" ){
            return error("保険者番号が入力されていません。");
        }
        let hihokenshaBangou = this.map.hihokenshaBangou.value;
        if( hihokenshaBangou.length !== 8 ){
            if( !confirm("被保険者番号が８桁でありませんがこのまま入力しますか？") ){
                return error("被保険者番号の入力が不適切です。");
            }
        }
        let validFromOpt = this.validFrom.get();
        if( !validFromOpt.ok ){
            return error(validFromOpt.message);
        }
        let validFrom = validFromOpt.value;
        let validUptoOpt = this.validUpto.get();
        if( !validUptoOpt.ok ){
            return error(validUptoOpt.message);
        }
        let validUpto = validUptoOpt.value;
        let futanWari = parseInt(getRadioValue(this.map.form, "futan-wari"));
        if( isNaN(futanWari) || !(futanWari >= 1 && futanWari <= 3) ){
            return error("負担割の値が不適切です。");
        }
        return success({
            koukikoureiId, patientId, hokenshaBangou, hihokenshaBangou,
            validFrom, validUpto, futanWari
        });
    }
}