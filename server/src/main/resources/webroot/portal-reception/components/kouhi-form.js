import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {DateInput} from "./date-input.js";
import {error, success} from "../js/opt-result.js";

let tmpl = `
<form class="x-form" onsubmit="return false;">
    <div class="form-group row">
        <div class="col-sm-2 col-form-label d-flex justify-content-end">負担者番号</div>
        <div class="col-sm-10 form-inline">
            <input type="text" class="form-control x-futansha"/>
        </div>
    </div>
    <div class="form-group row">
        <div class="col-sm-2 col-form-label d-flex justify-content-end">受給者番号</div>
        <div class="col-sm-10 form-inline">
            <input type="text" class="form-control x-jukyuusha"/>
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
</form>
`;

export class KouhiForm {
    constructor(kouhi){
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
        this.kouhiId = 0;
        this.patientId = 0;
        if( kouhi ){
            this.set(kouhi);
        }
    }

    setPatientId(patientId){
        this.patientId = patientId;
    }

    set(kouhi){
        this.kouhiId = kouhi.kouhiId;
        this.patientId = kouhi.patientId;
        this.map.futansha.value = kouhi.futansha;
        this.map.jukyuusha.value = kouhi.jukyuusha;
        this.validFrom.set(kouhi.validFrom);
        this.validUpto.set(kouhi.validUpto);
    }

    get(){
        let kouhiId = this.kouhiId;
        let patientId = this.patientId;
        if( !patientId ){
            return error("患者番号が設定されていません。");
        }
        let futansha = this.map.futansha.value;
        if( futansha === "" ){
            return error("負担者番号が入力されていません。");
        }
        let jukyuusha = this.map.jukyuusha.value;
        if( jukyuusha === "" ){
            return error("受給者番号が入力されていません。");
        }
        let validFromOpt = this.validFrom.get();
        if( !validFromOpt.ok ){
            return error("開始日：" + validFromOpt.message);
        }
        let validFrom = validFromOpt.value;
        let validUptoOpt = this.validUpto.get();
        if( !validUptoOpt.ok ){
            return error("終了日：" + validUptoOpt.message);
        }
        let validUpto = validUptoOpt.value;
        return success({
            kouhiId, patientId, futansha, jukyuusha,
            validFrom, validUpto
        });
    }
}