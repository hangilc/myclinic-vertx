import {parseElement} from "../js/parse-node.js";
import {DateInput} from "./date-input.js";
import {RadioGroup} from "./radio-group.js";

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
                <div class="col-sm-2 col-form-label d-flex justify-content-end">被保険者番号</div>
                <div class="col-sm-10 form-inline">
                    <input type="text" class="form-control x-hihokensha-bangou"/>
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
                <div class="col-sm-2 col-form-label d-flex justify-content-end">負担割</div>
                <div class="col-sm-10 form-inline">
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
    </div>
`;

export class KoukikoureiForm {
    constructor(ele) {
        if( !ele ){
            let wrapper = document.createElement("div");
            wrapper.innerHTML = tmpl;
            ele = wrapper.firstChild;
        }
        if( ele.children && ele.children.length === 0 ){
            ele.innerHTML = tmpl;
        }
        let map = parseElement(ele);
        this.ele = ele;
        this.error = null;
        this.hokenshaBangouElement = map.hokenshaBangou;
        this.hihokenshaBangouElement = map.hihokenshaBangou;
        this.validFromElement = new DateInput(map.validFrom);
        this.validUptoElement = new DateInput(map.validUpto);
        [this.validFromElement, this.validUptoElement].forEach(di => {
            di.setGengouList("令和", ["令和", "平成"])
        });
        this.validUptoElement.allowEmpty();
        this.futanWariElement = new RadioGroup(ele.querySelector("form"), "futan-wari");
    }

    set(koukikourei){
        if( koukikourei ){
            this.hokenshaBangouElement.value = koukikourei.hokenshaBangou;
            this.hihokenshaBangouElement.value = koukikourei.hihokenshaBangou;
            this.validFromElement.set(koukikourei.validFrom);
            this.validUptoElement.set(koukikourei.validUpto);
            this.futanWariElement.set(koukikourei.futanWari);
        } else {
            this.hokenshaBangouElement.value = "";
            this.hihokenshaBangouElement.value = "";
            this.validFromElement.set(null);
            this.validUptoElement.set(null);
            this.futanWariElement.set("1");
        }
        return this;
    }

    getError(){
        let err = this.error;
        this.error = null;
        return err;
    }

    clearValidUpto(){
        this.validUptoElement.clear();
    }

    get(koukikoureiId, patientId){
        let hokenshaBangouInput = this.hokenshaBangouElement.value;
        if( hokenshaBangouInput === "" ){
            this.error = "保険者番号が入力されていません。";
            return undefined;
        }
        if( hokenshaBangouInput.length !== 8 ){
            if( !confirm("保険者番号（後期高齢保険）が８桁でありませんが、そのまま入力しますか？") ){
                return undefined;
            }
        }
        let hokenshaBangou = hokenshaBangouInput;
        let hihokenshaBangouInput = this.hihokenshaBangouElement.value;
        if( hihokenshaBangouInput === "" ){
            this.error = "被保険者番号が入力されていません。";
            return undefined;
        }
        if( hihokenshaBangouInput.length !== 8 ){
            if( !confirm("被保険者番号（後期高齢保険）が８桁でありませんが、そのまま入力しますか？") ){
                return undefined;
            }
        }
        let hihokenshaBangou = hihokenshaBangouInput;
        let validFrom = this.validFromElement.get();
        if( !validFrom ){
            this.error = "開始日の入力が不適切です。";
            return undefined;
        }
        let validUpto = this.validUptoElement.val();
        if( !validUpto ){
            this.error = "終了日の入力が不適切です。";
            return undefined;
        }
        let futanWariInput = this.futanWariElement.get();
        let futanWari = parseInt(futanWariInput);
        if( !(futanWari >= 1 && futanWari <= 3) ){
            this.error = "負担割の入力が不適切です。";
            return undefined;
        }
        return {
            koukikoureiId,
            patientId,
            hokenshaBangou,
            hihokenshaBangou,
            validFrom,
            validUpto,
            futanWari
        };
    }
}