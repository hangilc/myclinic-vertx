import {createElementFrom} from "../../../js/create-element-from.js";
import {parseElement} from "../../../js/parse-node.js";
import {createDateInput} from "./date-input.js";
import * as DiseaseUtil from "../../js/disease-util.js";
import {click, getSelectedValue, on, setSelectedValue, submit} from "../../../js/dom-helper.js";
import * as funs from "./disease-funs.js";
import * as app from "../app.js";
import * as consts from "../../../js/consts.js";

let examples = [];

export function initExamples(examples_) {
    examples = examples_;
}

const tmpl = `
    <div>
        <div class="mb-2">
            名前：<span class="x-name"></span>
        </div>
        <div class="x-start-date"></div>
        <div>から</div>
        <div class="x-end-date mb-2"></div>
        <div class="form-inline mb-2">
            <select class="x-end-reason-select form-control">
                <option value="N">継続</option>
                <option value="C">治癒</option>
                <option value="S">中止</option>
                <option value="D">死亡</option>
            </select>
        </div>
        <div class="mt-1">
            <button type="button" class="x-enter btn btn-secondary btn-sm">入力</button>
            <a href="javascript:void(0)" class="x-susp">の疑い</a>
            <a href="javascript:void(0)" class="x-del-adj">修飾語削除</a>
            <a href="javascript:void(0)" class="x-clear-end-date" style="white-space: nowrap">終了日クリア</a>
            <a href="javascript:void(0)" class="x-delete">削除</a>
        </div>
        <div class="x-search">
            <form class="x-form mt-1" onsubmit="return false;">
                <div class="form-inline">
                    <input type="text" class="x-search-text form-control mr-2" 
                        style="width:60px; flex-grow: 1"/>
                    <button type="submit" class="btn btn-secondary btn-sm mr-2">検索</button>
                    <a href="javascript:void(0)" class="x-example">例</a>
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
        this.origDisease = disease.disease;
        this.ele = createElementFrom(tmpl);
        this.map = parseElement(this.ele);
        this.startDateInput = createDateInput();
        this.endDateInput = createDateInput();
        this.map.startDate.append(this.startDateInput.ele);
        this.map.endDate.append(this.endDateInput.ele);
        const self = this;
        this.props = {
            master: disease.master,
            adjList: disease.adjList.map(adj => adj.master),
            get startDate() {
                return self.startDateInput.get();
            },
            set startDate(value) {
                self.startDateInput.set(value);
            },
            get endDate() {
                return self.endDateInput.isCleared() ? "0000-00-00" : self.endDateInput.get();
            },
            set endDate(value) {
                if (value === "0000-00-00") {
                    self.endDateInput.clear();
                } else {
                    self.endDateInput.set(value);
                }
            },
            get endReason() {
                return getSelectedValue(self.map.endReasonSelect);
            },
            set endReason(value) {
                setSelectedValue(self.map.endReasonSelect, value);
            }
        }
        this.props.startDate = disease.disease.startDate;
        this.props.endDate = disease.disease.endDate;
        this.props.endReason = disease.disease.endReason;
        this.updateDisp();
        click(this.map.enter, async event => await this.doEnter());
        click(this.map.susp, event => {
            this.props.adjList.push(consts.suspMaster);
            this.updateDisp();
        });
        click(this.map.delAdj, event => {
            this.props.adjList = [];
            this.updateDisp();
        });
        click(this.map.clearEndDate, event => {
            this.endDateInput.clear();
            this.updateDisp();
        });
        click(this.map.delete, async event => await this.doDelete());
        submit(this.map.form, async event => await this.doSearch());
        click(this.map.example, event => this.doExample());
        on(this.map.select, "change", event => this.doSelect());
    }

    updateDisp() {
        console.log(this.props.adjList);
        this.map.name.innerText = DiseaseUtil.diseaseRepByMasters(
            this.props.master, this.props.adjList);
        this.startDateInput.set(this.props.startDate);
        {
            const endDate = this.props.endDate;
            if (!endDate || endDate === "0000-00-00") {
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

    getSearchKind() {
        return this.map.form.querySelector("input[name='search-kind']:checked").value;
    }

    async doEnter() {
        try {
            const disease = Object.assign({}, this.origDisease);
            const master = this.props.master;
            if (!master) {
                // noinspection ExceptionCaughtLocallyJS
                throw new Error("病名が選択されていません。");
            }
            disease.shoubyoumeicode = master.shoubyoumeicode;
            disease.startDate = this.props.startDate;
            disease.endReason = this.props.endReason;
            disease.endDate = this.props.endDate;
            if (disease.endReason === "N") {
                disease.endDate = "0000-00-00";
            } else {
                if( !disease.endDate || disease.endDate === "0000-00-00" ){
                    // noinspection ExceptionCaughtLocallyJS
                    throw new Error("終了日が指定されていません。");
                }
            }
            const shuushokugocodes = this.props.adjList.map(m => m.shuushokugocode);
            await app.rest.modifyDisease({disease, shuushokugocodes});
            await app.loadDiseases();
            this.ele.dispatchEvent(new CustomEvent("disease-changed", {bubbles: true}));
        } catch (e) {
            alert(e.toString());
        }
    }

    async doSearch() {
        const text = this.map.searchText.value.trim();
        if (!text) {
            return;
        }
        const date = this.startDateInput.get();
        if (!date) {
            return;
        }
        const searchKind = this.getSearchKind();
        await funs.search(text, date, searchKind, this.map.select);
    }

    doExample() {
        funs.setExamples(examples, this.map.select);
    }

    doSelect() {
        funs.handleSelected(this.map.select,
            master => {
                this.props.master = master;
                this.updateDisp();
            },
            master => {
                this.props.adjList.push(master);
                this.updateDisp();
            },
            async example => {
                try {
                    const date = this.startDateInput.get();
                    const resolved = await funs.resolveExample(example, date);
                    Object.assign(this.props, resolved);
                    this.updateDisp();
                } catch (e) {
                    alert(e.toString());
                }
            }
        );
    }

    async doDelete(){
        if( confirm("本当に、この病名を削除していいですか？") ){
            const diseaseId = this.origDisease.diseaseId;
            await app.rest.deleteDisease(diseaseId);
            this.ele.dispatchEvent(new CustomEvent("disease-deleted", {bubbles: true, detail: diseaseId}));
            await app.loadDiseases();
        }

    }

}