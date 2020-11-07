import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";
import * as DiseaseUtil from "../../js/disease-util.js";
import * as C from "../../../portal/js/consts.js";
import * as consts from "../../../portal/js/consts.js";

let html = `
<div>
    <div class="disease-name">名称：<span class="x-disease-name"></span></div>
    <div>
        開始日：<input type="date" class="x-start-date">
    </div>
    <div>
        <button class="x-enter">入力</button>
        <a href="javascript:void(0)" class="x-add-susp">の疑い</a> |
        <a href="javascript:void(0)" class="x-clear-adj">修飾語削除</a>
    </div>
    <div>
        <form class="x-form">
            <div>
                <input type="text" class="x-search-text">
                <button type="submit">検索</button>
                <a href="javascript:void(0)" class="x-examples-link">例</a>
            </div>
            <div> 
                <input type="radio" name="search-mode" value="name" checked>病名
                <input type="radio" name="search-mode" value="adj">修飾語
            </div>
        </form>
        <select size="12" class="x-select"></select>
    </div>
</div>
`;

export function createDiseaseAdd(diseaseFulls, visitedAt, patientId, examples, rest) {
    let ele = document.createElement("div");
    ele.innerHTML = html;
    let byoumeiMaster = null;
    let adjMasters = [];
    let map = parseElement(ele);
    map.startDate.value = visitedAt;
    map.addSusp.onclick = event => addAdjMaster(C.suspMaster);
    map.clearAdj.onclick = event => {
        adjMasters = [], updateName();
    };
    map.form.onsubmit = async event => {
        event.preventDefault();
        let text = map.searchText.value;
        let opts = await search(text, getSearchMode(), visitedAt, rest);
        map.select.innerHTML = "";
        opts.forEach(opt => map.select.append(opt));
    };
    map.examplesLink.onclick = async event => {
        await F.fillSelectWithDiseaseExampleOptions(map.select, rest);
    };
    F.setupSearchDiseaseResultHandler(map.select,
        byoumeiMaster => setByoumeiMaster(byoumeiMaster),
        adjMaster => addAdjMaster(adjMaster),
        async example => await setByExample(example)
    );
    map.enter.onclick = async event => {
        let req = composeReq(patientId, byoumeiMaster, getStartDate(), adjMasters);
        if (req) {
            let diseaseId = await rest.enterDisease(req);
            let entered = await rest.getDisease(diseaseId);
            ele.dispatchEvent(F.event("disease-entered", entered));
            byoumeiMaster = null;
            adjMasters = [];
            updateName();
        }
    };
    return ele;

    function updateName() {
        map.diseaseName.innerText = DiseaseUtil.diseaseRepByMasters(byoumeiMaster, adjMasters);
    }

    function setByoumeiMaster(master) {
        byoumeiMaster = master;
        updateName();
    }

    function addAdjMaster(master) {
        adjMasters.push(master);
        updateName();
    }

    async function setByExample(example) {
        let at = getStartDate () || F.todayAsSqldate();
        let result = await F.resolveMastersOfDiseaseExample(example, at, rest);
        byoumeiMaster = result.byoumeiMaster;
        adjMasters = result.adjMasters;
        updateName();
    }

    function getSearchMode() {
        return ele.querySelector("form input[type=radio][name=search-mode]:checked").value;
    }

    function getStartDate() {
        return map.startDate.value;
    }
}

function composeReq(patientId, byoumeiMaster, startDate, adjMasters) {
    if (!(patientId > 0)) {
        alert("No patientId");
        return null;
    }
    if (!byoumeiMaster) {
        alert("病名が指定されていません。");
        return null;
    }
    if (!startDate) {
        alert("開始日が指定されていません。");
        return null;
    }
    let disease = {
        patientId,
        shoubyoumeicode: byoumeiMaster.shoubyoumeicode,
        startDate,
        endReason: consts.DiseaseEndReasonNotEnded,
        endDate: "0000-00-00"
    };
    let adjList = adjMasters.map(m => {
        return {shuushokugocode: m.shuushokugocode};
    });
    return {disease, adjList};
}

async function search(text, mode, at, rest) {
    if (mode === "name") {
        return await searchByoumei(text, at, rest);
    } else if (mode === "adj") {
        return await searchAdj(text, at, rest);
    }
}

async function searchByoumei(text, at, rest) {
    let result = await rest.searchByoumeiMaster(text, at);
    return result.map(m => {
        let opt = F.createOption(m.name, m);
        opt.dataset.kind = "name";
        opt.data = m;
        return opt;
    })
}

async function searchAdj(text, at, rest) {
    let result = await rest.searchShuushokugoMaster(text, at);
    return result.map(m => {
        let opt = F.createOption(m.name, m);
        opt.dataset.kind = "adj";
        opt.data = m;
        return opt;
    })
}