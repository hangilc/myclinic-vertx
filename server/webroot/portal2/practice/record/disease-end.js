import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";
import * as DiseaseUtil from "../../js/disease-util.js";
import * as consts from "../../../portal/js/consts.js";

let html = `
<div class="x-diseases"></div>
<div>
    <input type="date" class="x-end-date">
</div>
<div>
    <form onsubmit="return false;" class="x-end-reason-form"> 
        <input type="radio" name="end-reason" value="C" checked> 治癒
        <input type="radio" name="end-reason" value="S"> 中止
        <input type="radio" name="end-reason" value="D"> 死亡
    </form>
    <div>
        <button class="x-enter">入力</button>
    </div>
</div>
`;

export function createDiseaseEnd(diseaseFulls, patientId, rest) {
    let ele = document.createElement("div");
    ele.innerHTML = html;
    let map = parseElement(ele);
    updateDiseaseDisplay();
    map.enter.onclick = async event => {
        let selected = listSelectedDiseases();
        let endDate = getEndDate();
        let endReason = getEndReason();
        if( !endDate ){
            alert("終了日が指定されていません。");
            return;
        }
        let req = createReq(selected, endDate, endReason);
        await rest.batchUpdateDiseaseEndReason(req);
        let cur = await rest.listCurrentDisease(patientId);
        ele.dispatchEvent(F.event("current-diseases-changed", cur));
        diseaseFulls = cur;
        updateDiseaseDisplay();
    };
    return ele;

    function updateDiseaseDisplay(){
        map.diseases.innerHTML = "";
        diseaseFulls.forEach(df => {
            let rep = DiseaseUtil.diseaseFullRep(df);
            let check = F.createCheckbox(rep, "", df);
            map.diseases.append(check);
        });
        let checkboxes = map.diseases.querySelectorAll("input[type=checkbox]");
        checkboxes.forEach(chk => {
            chk.onclick = event => {
                let endDate = maxStartDate(checkboxes);
                setEndDate(endDate);
            };
        });
    }

    function setEndDate(date) {
        map.endDate.value = date;
    }

    function getEndDate() {
        return map.endDate.value;
    }

    function getEndReason() {
        return map.endReasonForm.querySelector("input[type=radio]:checked").value;
    }

    function listSelectedDiseases() {
        let checkboxes = map.diseases.querySelectorAll("input[type=checkbox]");
        return Array.from(checkboxes).filter(chk => chk.checked).map(chk => chk.data);
    }
}

function createReq(diseaseFulls, endDate, endReason) {
    return diseaseFulls.map(diseaseFull => {
        if (endReason === consts.DiseaseEndReasonCured && containsSusp(diseaseFull)) {
            endReason = consts.DiseaseEndReasonStopped;
        }
        return {
            diseaseId: diseaseFull.disease.diseaseId,
            endDate,
            endReason
        }
    });
}

function containsSusp(diseaseFull) {
    let suspcode = consts.suspMaster.shuushokugocode;
    if (diseaseFull.adjList) {
        for (let adjFull of diseaseFull.adjList) {
            if (adjFull.diseaseAdj.shuushokugocode === suspcode) {
                return true;
            }
        }
    }
    return false;
}

function maxStartDate(checks) {
    let date = null;
    checks.forEach(chk => {
        if (chk.checked) {
            let df = chk.data;
            if (date === null || df.disease.startDate > date) {
                date = df.disease.startDate;
            }
        }
    });
    return date;
}
