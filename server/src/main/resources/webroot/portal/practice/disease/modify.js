import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {createDateInput} from "./date-input.js";
import * as DiseaseUtil from "../../js/disease-util.js";

const tmpl = `
    <div>
        <div>
            名前：<span class="x-name"></span>
        </div>
        <div class="x-start-date"></div>
        <div>から</div>
        <div class="x-end-date"></div>
        <div class="form-inline">
            <select class="x-end-reason-select form-control">
                <option value="N">継続</option>
                <option value="C">治癒</option>
                <option value="S">中止</option>
                <option value="D">死亡</option>
            </select>
        </div>
        <div class="mt-1">
            <button type="button" class="x-enter btn btn-secondary">入力</button>
            <a href="javascript:void(0)" class="x-susp">の疑い</a>
            <a href="javascript:void(0)" class="x-del-adj">修飾語削除</a>
            <a href="javascript:void(0)" class="x-clear-end-date">終了日クリア</a>
            <a href="javascript:void(0)" class="x-delete">削除</a>
        </div>
        <div class="x-search_">
            <form class="x-form mt-1">
                <div class="form-inline">
                    <input type="text" class="x-search-text form-control"/>
                    <button type="submit" class="btn btn-secondary mt-1">検索</button>
                    <a href="javascript:void(0)" class="x-example mt-1 ml-2">例</a>
                </div>
                <div class="mt-1" onsubmit="return false;">
                    <input type="radio" name="search-kind"
                           value="byoumei" checked> 病名
                    <input type="radio" name="search-kind"
                           value="adj"> 修飾語
                </div>
            </form>
            <div class="mt-1">
                <select size="10" class="x-select form-control"></select>
            </div>
        </div>
    </div>
`;

export class Modify {
    constructor(disease) {
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.startDateInput = createDateInput();
        this.endDateInput = createDateInput();
        this.map.startDate.append(this.startDateInput.ele);
        this.map.endDate.append(this.endDateInput.ele);
        this.props = {
            master: disease.master,
            adjList: disease.adjList,
            startDate: disease.disease.startDate,
            endDate: disease.disease.endDate,
            endReason: disease.disease.endReason
        }
        this.updateDisp();
    }

    updateDisp(){
        this.map.name.innerText = DiseaseUtil.diseaseRepByMasters(
            this.props.master, this.props.adjList);
        this.startDateInput.set(this.props.startDate);
        {
            const endDate = this.props.endDate;
            if( !endDate || endDate === "0000-00-00" ){
                this.endDateInput.clear();
            } else {
                this.endDateInput.set(endDate);
            }
        }
        {
            const endReason = this.props.endReason;
            const opt = this.map.endReasonSelect.querySelector(`option[value='${endReason}']`);
            opt.selected = true;
        }

    }

}