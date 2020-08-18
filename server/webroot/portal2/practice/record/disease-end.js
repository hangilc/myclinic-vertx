import {parseElement} from "../../js/parse-element.js";
import * as F from "../functions.js";
import * as DiseaseUtil from "../../js/disease-util.js";
import * as consts from "../../../portal/js/consts.js";

let html = `
<div class="x-diseases"></div>
<div>
    <div>
        <input type="date" class="x-end-date">
    </div>
    <div> 
        <a href="javascript:void(0)" class="x-move-day">日</a> |
        <a href="javascript:void(0)" class="x-move-week">週</a> |
        <a href="javascript:void(0)" class="x-move-month">月</a> |
        <a href="javascript:void(0)" class="x-move-to-today">今日</a> |
        <a href="javascript:void(0)" class="x-move-to-month-end">月末</a> |
        <a href="javascript:void(0)" class="x-move-to-prev-month-end">先月末</a>
    </div>
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
    setupDateMovers(map);
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

function moveDay(date, shiftKey){
    let n = shiftKey ? -1 : 1;
    date.setDate(date.getDate() + n);
}

function moveWeek(date, shiftKey){
    let n = shiftKey ? -7 : 7;
    date.setDate(date.getDate() + n);
}

function moveMonth(date, shiftKey){
    let n = shiftKey ? -1 : 1;
    let probe = new Date(date.getFullYear(), date.getMonth() + n, 1);
    let probeLastDay = F.getLastDayOfMonth(probe);
    let day = date.getDate();
    let lastDay = F.getLastDayOfMonth(date);
    if( day > probeLastDay || day === lastDay ){
        day = probeLastDay;
    }
    date.setFullYear(probe.getFullYear());
    date.setMonth(probe.getMonth());
    date.setDate(day);
}

function moveToToday(date, shiftKey){

}

function moveToMonthEnd(date, shiftKey){

}

function moveToPrevMonthEnd(date, shiftKey){

}

function setupDateMovers(map){
    setup(map.moveDay, moveDay);
    setup(map.moveWeek, moveWeek);
    setup(map.moveMonth, moveMonth);
    setup(map.moveToToday, moveToToday);
    setup(map.moveToMonthEnd, moveToMonthEnd);
    setup(map.moveToPrevMonthEnd, moveToPrevMonthEnd);

    function setup(ele, f){
        ele.onclick = event => {
            let d = getCurrent();
            if( d ){
                f(d, event.shiftKey);
                setCurrent(d);
            }
        }
    }

    function setCurrent(date){
        map.endDate.value = F.dateToSqldate(date);
    }

    function getCurrent(){
        let val = map.endDate.value;
        if( val ){
            return new Date(val);
        } else {
            null;
        }
    }
}

