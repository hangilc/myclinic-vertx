import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {createDateInput} from "./date-input.js";
import * as DiseaseUtil from "../../js/disease-util.js";
import {gensymId} from "../../../js/gensym-id.js";
import {on, click} from "../../../js/dom-helper.js";
import * as consts from "../../js/consts.js";
import * as app from "../app.js";

const tmpl = `
    <div>
        <div class="mb-2 x-list"></div>
        <div>
            <div class="mb-2 x-date-input-wrapper"></div>
            <div class="mb-2 x-date-commands">
                <a href="javascript:void(0)" class="x-advance-week">週</a>
                <a href="javascript:void(0)" class="x-today">今日</a>
                <a href="javascript:void(0)" class="x-end-of-month">月末</a>
                <a href="javascript:void(0)" class="x-end-of-last-month">先月末</a>
            </div>
        </div>
        <div class="mb-2">
            <form class="form-inline x-end-reason-form" onsubmit="return false">
                転機：
                <input type="radio" name="end-reason" value="C" checked>
                <span class="ml-1">治癒</span>
                <input type="radio" name="end-reason" value="S" class="ml-2">
                <span class="ml-1">中止</span>
                <input type="radio" name="end-reason" value="D" class="ml-2">
                <span class="ml-1">死亡</span>
            </form>
        </div>
        <div>
            <button type="button" class="x-enter btn btn-primary btn-sm">入力</button>
        </div>
    </div>
`;

// noinspection ExceptionCaughtLocallyJS
export class End {
    constructor(diseases) {
        const self = this;
        this.props = {
            diseases,
            get date() {
                try {
                    return self.dateInput.get();
                } catch(e){
                    throw new Error("終了日：" + e.message);
                }
            },
            set date(value) {
                self.dateInput.set(value)
            },
            get endReason(){
                return self.map.endReasonForm.querySelector("input[name='end-reason']:checked").value;
            }
        };
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.dateInput = createDateInput();
        this.map.dateInputWrapper.append(this.dateInput.ele);
        click(this.map.enter, async event => await this.doEnter());
        on(this.map.list, "item-check-changed", event => {
            const dates = this.listCheckedDate();
            if( dates.length > 0 ){
                const lastDate = dates[dates.length - 1];
                this.props.date = lastDate;
            } else {
                self.dateInput.clear();
            }
        });
        this.updateListUI();
    }

    updateListUI() {
        const wrapper = this.map.list;
        this.props.diseases.forEach(diseaseFull => {
            const item = new Item(diseaseFull);
            wrapper.append(item.ele);
        });
    }

    listCheckedInputs(){
        return Array.from(this.map.list.querySelectorAll(".disease-end-item input:checked"));
    }

    listCheckedDate(){
        return this.listCheckedInputs().map(input => input.dataset.date);
    }

    listCheckedDisease(){
        return this.listCheckedInputs().map(input => input.data);
    }

    async doEnter(){
        try {
            const checked = this.listCheckedDisease();
            if( checked.length > 0 ){
                let endDate = this.props.date;
                let endReason = this.props.endReason;

            }
            const reqs = checked.map(df => convertToReq(df, this.props.endReason, this.props.date));
            await app.rest.batchUpdateDiseaseEndReason(reqs);
            await app.loadDiseases();
            this.ele.dispatchEvent(new CustomEvent("disease-end-reason-changed", {bubbles: true}));
        } catch(e){
            alert(e.toString());
        }
    }

    checkedDiseases() {

    }
}

const itemTmpl = `
    <div class="form-group form-check disease-end-item mb-0">
        <input type="checkbox" class="form-check-input" id="gensym-disease" data-date="""/>
        <label class="form-check-label" for="gensym-disease"></label>        
    </div>
`;

class Item {
    constructor(diseaseFull) {
        this.ele = createElementFrom(itemTmpl);
        this.input = this.ele.querySelector("input");
        on(this.input, "change", event => this.ele.dispatchEvent(
            new CustomEvent("item-check-changed", {bubbles: true})));
        this.ele.querySelector("label").innerText =
            DiseaseUtil.diseaseRep(diseaseFull) + " " + DiseaseUtil.datePart(diseaseFull.disease);
        gensymId(this.ele);
        this.input.dataset.date = diseaseFull.disease.startDate;
        this.input.data = diseaseFull;
    }
}

function convertToReq(diseaseFull, endReason, endDate) {
    if (endReason === consts.DiseaseEndReasonCured && DiseaseUtil.containsSusp(diseaseFull)) {
        endReason = consts.DiseaseEndReasonStopped;
    }
    return {
        diseaseId: diseaseFull.disease.diseaseId,
        endDate,
        endReason
    }
}



