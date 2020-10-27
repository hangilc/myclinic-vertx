import {parseElement} from "../js/parse-node.js";
import {DateInput} from "./date-input.js";

let tmpl = `
    <div class="mt-4">
        <form>
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
    </div>
`;

export class KouhiForm {
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
        this.error = null;
        this.futanshaElement = map.futansha;
        this.jukyuushaElement = map.jukyuusha;
        this.validFromElement = new DateInput(map.validFrom);
        this.validUptoElement = new DateInput(map.validUpto);
        [this.validFromElement, this.validUptoElement].forEach(e => e.setGengouList(
            "令和",
            ["令和", "平成"]
        ));
        this.validUptoElement.allowEmpty();
    }

    set(kouhi){
        this.futanshaElement.value = kouhi.futansha;
        this.jukyuushaElement.value = kouhi.jukyuusha;
        this.validFromElement.set(kouhi.validFrom);
        this.validUptoElement.set(kouhi.validUpto);
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

    get(kouhiId, patientId){
        let futanshaInput = this.futanshaElement.value;
        if( futanshaInput === "" ){
            this.error = "負担者番号が入力されていません。";
            return undefined;
        }
        let futansha = parseInt(futanshaInput);
        if( isNaN(futansha) ){
            this.error = "負担者番号の入力が不適切です。";
            return undefined;
        }
        let jukyuushaInput = this.jukyuushaElement.value;
        if( jukyuushaInput === "" ){
            this.error = "受給者番号が入力されていません。";
            return undefined;
        }
        let jukyuusha = parseInt(jukyuushaInput);
        if( isNaN(jukyuusha) ){
            this.error = "受給者番号の入力が不適切です。";
            return undefined;
        }
        let validFrom = this.validFromElement.get();
        if( !validFrom ){
            console.log(this.validFromElement.getError());
            this.error = "開始日の入力が不適切です。";
            return undefined;
        }
        let validUpto = this.validUptoElement.get();
        if( !validUpto ){
            this.error = "終了日の入力が不適切です。";
            return undefined;
        }
        return {
            kouhiId,
            patientId,
            futansha,
            jukyuusha,
            validFrom,
            validUpto
        };
    }
}
