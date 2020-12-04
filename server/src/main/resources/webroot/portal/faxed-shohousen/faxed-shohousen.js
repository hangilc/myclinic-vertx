let html = `
<form>
    <div class="form-inline">
        <span>開始日</span>
        <input type="date" class="form-control ml-1" id="faxed-from-date-input"/>
        <span class="ml-2">終了日</span>
        <input type="date" class="form-control ml-1" id="faxed-upto-date-input"/>
    </div>
    <div class="form-inline mt-2">
        <span class="mr-2">ラベル開始</span>
        <label class="col-form-label">行</label>
        <input type="number" class="form-control col-2 ml-1" id="faxed-row-input" value="1"/>
        <label class="col-form-label ml-2">列</label>
        <input type="number" class="form-control col-2 ml-1" id="faxed-col-input" value="1"/>
    </div>
    <div class="form-inline mt-2">
        <button type="button" class="btn btn-primary form-control" id="faxed-create-button">すべて作成</button>
    </div>
</form>

<div id="faxed-progress-report" class="alert alert-info alert-dismissible my-3 d-none" role="alert">
    <div class="faxed-part-message"></div>
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
    </button>
</div>

<div id="faxed-error-report" class="card mt-3 d-none">
    <div class="card-header text-white bg-danger">エラー</div>
    <div class="card-body"></div>
</div>

<div id="faxed-data-file-status" class="faxed-group-status card mt-3">
    <div class="card-header">データ</div>
    <div class="card-body">
        <div class="faxed-part-message"></div>
        <div class="form-inline mt-2">
            <button type="button" class="btn btn-link faxed-part-create">作成</button>
        </div>
    </div>
</div>

<div id="faxed-shohousen-pdf-status" class="faxed-group-status card mt-3">
    <div class="card-header">処方箋ＰＤＦ</div>
    <div class="card-body">
        <div class="faxed-part-message"></div>
        <div class="form-inline mt-2">
            <button type="button" class="btn btn-link faxed-part-create">作成</button>
            <button type="button" class="btn btn-link faxed-part-display">表示</button>
        </div>
    </div>
</div>

<div id="faxed-pharma-letter-pdf-status" class="faxed-group-status card mt-3">
    <div class="card-header">薬局レターＰＤＦ</div>
    <div class="card-body">
        <div class="faxed-part-message"></div>
        <div class="form-inline mt-2">
            <button type="button" class="btn btn-link faxed-part-create">作成</button>
            <button type="button" class="btn btn-link faxed-part-display">表示</button>
        </div>
    </div>
</div>

<div id="faxed-pharma-label-pdf-status" class="faxed-group-status card mt-3">
    <div class="card-header">薬局住所ラベルＰＤＦ</div>
    <div class="card-body">
        <div class="faxed-part-message"></div>
        <div class="form-inline mt-2">
            <button type="button" class="btn btn-link faxed-part-create">作成</button>
            <button type="button" class="btn btn-link faxed-part-display">表示</button>
        </div>
    </div>
</div>

<div id="faxed-clinic-label-pdf-status" class="faxed-group-status card mt-3">
    <div class="card-header">クリニック住所ラベルＰＤＦ</div>
    <div class="card-body">
        <div class="faxed-part-message"></div>
        <div class="form-inline mt-2">
            <button type="button" class="btn btn-link faxed-part-create">作成</button>
            <button type="button" class="btn btn-link faxed-part-display">表示</button>
        </div>
    </div>
</div>

<ul class="nav nav-tabs mt-3">
    <li class="nav-item">
        <a class="nav-link faxed-part-prev-groups" href="javascript:void(0)">処理済一覧</a>
    </li>
</ul>
<div id="faxed-nav-content-prev-groups" class="faxed-nav-content d-none"></div>
`;

export function getHtml(){
    return html;
}

export async function initFaxedShohousen(pane, rest) {

    function shohousenUrl(path, attr=null) {
        let url = "/integration/faxed-shohousen-data/" + path;
        if( attr ){
            url += "?" + $.param(attr);
        }
        console.log("shohousenUrl", url);
        return url;
    }

    function getPrevGroups() {
        return ajaxGet(shohousenUrl("list-groups"));
    }

    function getLastGroup() {
        return ajaxGet(shohousenUrl("get-last-group"));
    }

    function getGroup(from, upto) {
        return ajaxGet(shohousenUrl("get-group") + `?from=${from}&upto=${upto}`)
    }

    function countShohousen(from, upto) {
        return ajaxGet(shohousenUrl("count-shohousen") + `?from=${from}&upto=${upto}`);
    }

    function createData(from, upto) {
        return ajaxPost(shohousenUrl("create-data") + `?from=${from}&upto=${upto}`);
    }

    function createShohousenText(from, upto) {
        return ajaxPost(shohousenUrl("create-shohousen-text") + `?from=${from}&upto=${upto}`);
    }

    function createShohousenPdf(from, upto) {
        return ajaxPost(shohousenUrl("create-shohousen-pdf") + `?from=${from}&upto=${upto}`);
    }

    function createPharmaLetterText(from, upto) {
        return ajaxPost(shohousenUrl("create-pharma-letter-text") + `?from=${from}&upto=${upto}`);
    }

    function createPharmaLetterPdf(from, upto) {
        return ajaxPost(shohousenUrl("create-pharma-letter-pdf") + `?from=${from}&upto=${upto}`);
    }

    function createPharmaLabelPdf(from, upto, row, col) {
        return ajaxPost(shohousenUrl("create-pharma-label-pdf") +
            `?from=${from}&upto=${upto}&row=${row}&col=${col}`);
    }

    function createClinicLabelPdf(from, upto, row, col, n) {
        return ajaxPost(shohousenUrl("create-clinic-label-pdf") +
            `?from=${from}&upto=${upto}&row=${row}&col=${col}&n=${n}`);
    }

    function setFromValue(from) {
        $("#faxed-from-date-input").val(from);
    }

    function getFromValue(from) {
        return $("#faxed-from-date-input").val();
    }

    function setUptoValue(upto) {
        $("#faxed-upto-date-input").val(upto);
    }

    function getUptoValue(upto) {
        return $("#faxed-upto-date-input").val();
    }

    function getRowValue() {
        let val = parseInt($("#faxed-row-input").val());
        if (isNaN(val)) {
            return null;
        } else {
            return val;
        }
    }

    function getColValue() {
        let val = parseInt($("#faxed-col-input").val());
        if (isNaN(val)) {
            return null;
        } else {
            return val;
        }
    }

    function getDateInputsEx() {
        let from = getFromValue();
        let err = [];
        if (!from) {
            err.push("開始日が設定されていません。");
        }
        let upto = getUptoValue();
        if (!upto) {
            err.push("終了日が設定されていません。");
        }
        let eles = err.map(e => $("<div>").text(e));
        if (eles.length > 0) {
            errorReport.setBody(...eles);
        }
        return [from, upto];
    }

    function getRowAndColInputEx() {
        let err = []
        let row = getRowValue();
        if (row == null) {
            err.push("行の値が不適切です。");
        }
        let col = getColValue();
        if (col == null) {
            err.push("列の値が不適切です。")
        }
        let eles = err.map(msg => $("<div>").text(msg));
        if (eles.length > 0) {
            errorReport.setBody(...eles);
        }
        return [row, col];
    }

    $("#faxed-create-button").on("click", event => createAll());

    class ProgressReport {
        constructor() {
            this.ele = $("#faxed-progress-report");
        }

        hide() {
            this.ele.addClass("d-none");
        }

        show() {
            this.ele.removeClass("d-none");
        }

        report(message) {
            this.ele.find(".faxed-part-message").text(message);
            if (message) {
                this.show();
            } else {
                this.hide();
            }
        }
    }

    let progressReport = new ProgressReport();

    class ErrorReport {
        constructor() {
            this.ele = $("#faxed-error-report");
        }

        show() {
            this.ele.removeClass("d-none");
        }

        hide() {
            this.ele.addClass("d-none");
        }

        findBody() {
            return this.ele.find(".card-body");
        }

        setBody(...elements) {
            let body = this.findBody().html("");
            elements.forEach(e => {
                if( typeof e === "string" ){
                    e = $("<pre>").text(e);
                }
                body.append(e)
            });
            if (elements.length > 0) {
                this.show();
            } else {
                this.hide();
            }
        }
    }

    let errorReport = new ErrorReport();

    class StatusReport {
        constructor(ele, prefix) {
            this.ele = ele;
            this.prefix = prefix;
            this.doCreate = () => {};
            this.displayLinkFun = null;
            ele.find(".faxed-part-create").on("click", () => this.doCreate());
            ele.find(".faxed-part-display").on("click", () => this.doDisplay());
        }

        setMessage(message) {
            this.ele.find(".faxed-part-message").text(message);
        }

        updateByObject(groupInfo) {
            this.update(groupInfo[this.prefix], groupInfo[this.prefix + "Size"],
                groupInfo[this.prefix + "LastModifiedAt"])
        }

        update(file, size, modifiedAt) {
            let text = "";
            if (file) {
                text = `作成済。file: ${file}, size: ${size}, modifiedAt: ${modifiedAt}`;
            } else {
                text = "未作成。";
            }
            this.setMessage(text);
        }

        setDoCreate(f) {
            this.doCreate = f;
            return this;
        }

        doDisplay() {
            if( this.displayLinkFun ){
                let [from, upto] = getDateInputsEx();
                if( from && upto ){
                    let url = this.displayLinkFun(from, upto);
                    window.open(url, "_blank");
                }
            }
        }

        setDisplayLinkFun(linkFun) {
            this.displayLinkFun = linkFun;
            return this;
        }

    }

    let dataStatusReport = new StatusReport($("#faxed-data-file-status"),
        "dataFile")
        .setDoCreate(async () => await createDataFileEx());
    let shohousenPdfStatusReport = new StatusReport($("#faxed-shohousen-pdf-status"),
        "shohousenPdfFile")
        .setDoCreate(async () => (await createShohousenTextFileEx()) &&
            (await createShohousenPdfFileEx()))
        .setDisplayLinkFun((from, upto) => shohousenUrl("shohousen-pdf", {from, upto}));
    let pharmaLetterPdfStatusReport = new StatusReport($("#faxed-pharma-letter-pdf-status"),
        "pharmaLetterPdfFile")
        .setDoCreate(async () => (await createPharmaLetterTextFileEx()) &&
            (await createPharmaLetterPdfFileEx()))
        .setDisplayLinkFun((from, upto) => shohousenUrl("pharma-letter-pdf", {from, upto}));
    let pharmaLabelPdfStatusReport = new StatusReport($("#faxed-pharma-label-pdf-status"),
        "pharmaLabelPdfFile")
        .setDoCreate(async () => await createPharmaLabelPdfFileEx())
        .setDisplayLinkFun((from, upto) => shohousenUrl("pharma-label-pdf", {from, upto}));
    let clinicLabelPdfStatusReport = new StatusReport($("#faxed-clinic-label-pdf-status"),
        "clinicLabelPdfFile")
        .setDoCreate(async () => await createClinicLabelPdfFileEx())
        .setDisplayLinkFun((from, upto) => shohousenUrl("clinic-label-pdf", {from, upto}));

    function isNoPharmaError(err){
        let regex = /Exception: presc without pharma.*<Visit\(.*visit_id='(\d+)'/
        let m = err.match(regex);
        if( m ){
            return {visitId: parseInt(m[1])};
        } else {
            return null;
        }
    }

    async function createNoPharmaCorrectForm(visitId){
        let wrap = $("<div>");
        let visit = await rest.getVisit(visitId);
        let patient = await rest.getPatient(visit.patientId);
        let msg = "処方箋情報がありません。<br/>" +
            `患者：${patient.lastName}${patient.firstName} (${patient.patientId})<br/>` +
            `受診日：${visit.visitedAt}`;
        wrap.text(msg);
        return wrap;
    }

    async function createDataFileEx() { // returns true if success
        let [from, upto] = getDateInputsEx();
        if (!(from && upto)) {
            return false;
        }
        progressReport.report("データを作成しています...");
        let result = await createData(from, upto);
        progressReport.report(null);
        if (!result.success) {
            console.log(result);
            if( result.kind === "missing-pharmacy" ){
                let message = result.message;
                let visitId = parseInt(result["visit-id"]);
                let visit = await rest.getVisit(visitId);
                let patient = await rest.getPatient(visit.patientId);
                errorReport.setBody(`${message}; (${patient.patientId})${patient.lastName}${patient.firstName}; ${visit.visitedAt}`);
            } else {
                alert(result);
            }
            // let m = isNoPharmaError(result.errorMessage);
            // if( m ){
            //     let form = await createNoPharmaCorrectForm(m.visitId);
            //     errorReport.setBody(form);
            // } else {
            //     errorReport.setBody(result.errorMessage);
            // }
            // dataStatusReport.updateByObject(await getGroup(from, upto));
            return false;
        } else {
            dataStatusReport.updateByObject(result);
            return true;
        }
    }

    async function createShohousenTextFileEx() { // returns true if success
        let [from, upto] = getDateInputsEx();
        if (!(from && upto)) {
            return false;
        }
        progressReport.report("処方箋テキストを作成しています...");
        let result = await createShohousenText(from, upto);
        progressReport.report(null);
        if (!result.success) {
            errorReport.setBody(result.errorMessage);
            return false;
        } else {
            return true;
        }
    }

    async function createShohousenPdfFileEx() { // returns true if success
        let [from, upto] = getDateInputsEx();
        if (!(from && upto)) {
            return false;
        }
        progressReport.report("処方箋ＰＤＦを作成しています...");
        let result = await createShohousenPdf(from, upto);
        progressReport.report(null);
        if (!result.success) {
            errorReport.setBody(result.errorMessage);
            shohousenPdfStatusReport.updateByObject(await getGroup(from, upto));
            return false;
        } else {
            shohousenPdfStatusReport.updateByObject(result);
            return true;
        }
    }

    async function createPharmaLetterTextFileEx() { // returns true if success
        let [from, upto] = getDateInputsEx();
        if (!(from && upto)) {
            return false;
        }
        progressReport.report("薬局レターのテキストを作成しています...");
        let result = await createPharmaLetterText(from, upto);
        progressReport.report(null);
        if (!result.success) {
            errorReport.setBody(result.errorMessage);
            return false;
        } else {
            return true;
        }
    }

    async function createPharmaLetterPdfFileEx() { // returns true if success
        let [from, upto] = getDateInputsEx();
        if (!(from && upto)) {
            return false;
        }
        progressReport.report("薬局レターＰＤＦを作成しています...");
        let result = await createPharmaLetterPdf(from, upto);
        progressReport.report(null);
        if (!result.success) {
            errorReport.setBody(result.errorMessage);
            pharmaLetterPdfStatusReport.updateByObject(await getGroup(from, upto));
            return false;
        } else {
            pharmaLetterPdfStatusReport.updateByObject(result);
            return true;
        }
    }

    async function createPharmaLabelPdfFileEx() { // returns true if success
        let [from, upto] = getDateInputsEx();
        if (!(from && upto)) {
            return false;
        }
        let [row, col] = getRowAndColInputEx();
        if (row == null || col == null) {
            return false;
        }
        progressReport.report("薬局住所ラベルＰＤＦを作成しています...");
        let result = await createPharmaLabelPdf(from, upto, row, col);
        progressReport.report(null);
        if (!result.success) {
            let msg = result.errorMessage || result.stdErr;
            errorReport.setBody(msg);
            pharmaLabelPdfStatusReport.updateByObject(await getGroup(from, upto));
            return false;
        } else {
            pharmaLabelPdfStatusReport.updateByObject(result);
            return true;
        }
    }

    async function createClinicLabelPdfFileEx() { // returns true if success
        let [from, upto] = getDateInputsEx();
        if (!(from && upto)) {
            return false;
        }
        let [row, col] = getRowAndColInputEx();
        if (row == null || col == null) {
            return false;
        }
        let n = await countShohousen(from, upto);
        if (!n) {
            errorReport.setBody("処方箋数を取得できません。");
            return false;
        }
        progressReport.report("クリニック住所ラベルＰＤＦを作成しています...");
        let result = await createClinicLabelPdf(from, upto, row, col, n);
        progressReport.report(null);
        if (!result.success) {
            errorReport.setBody(result.errorMessage);
            clinicLabelPdfStatusReport.updateByObject(await getGroup(from, upto));
            return false;
        } else {
            clinicLabelPdfStatusReport.updateByObject(result);
            return true;
        }
    }

    async function createAll() {
        let dataFileOk = await createDataFileEx();
        let shohousenPdfOk = dataFileOk && (await createShohousenTextFileEx()) &&
            (await createShohousenPdfFileEx());
        let pharmaLetterPdfOk = dataFileOk && (await createPharmaLetterTextFileEx()) &&
            (await createPharmaLetterPdfFileEx());
        let pharmaLabelPdfOk = dataFileOk && (await createPharmaLabelPdfFileEx());
        let clinicLabelPdfOk = dataFileOk && (await createClinicLabelPdfFileEx());
        if (dataFileOk && shohousenPdfOk && pharmaLetterPdfOk && pharmaLabelPdfOk && clinicLabelPdfOk) {
            progressReport.report("すべて作成しました。");
        }
    }

    function guessNextDates(lastUpdate) {
        let momentLast = moment(lastUpdate);
        let momentStart = momentLast.add(1, "day");
        let momentEnd = momentStart.clone();
        if (momentStart.date() < 15) {
            momentEnd.date(15);
        } else {
            momentEnd = momentEnd.endOf("month");
        }
        return [momentStart.format("YYYY-MM-DD"), momentEnd.format("YYYY-MM-DD")];
    }

    async function guessNextTarget() {
        let lastGroup = await getLastGroup();
        if (lastGroup) {
            if (lastGroup.completed) {
                let target = {};
                [target.from, target.upto] = guessNextDates(lastGroup.upto);
                return target;
            } else {
                return lastGroup;
            }
        } else {
            return {};
        }
    }

    function packedToSqlDate(packed){
        let m = /^(\d{4})(\d{2})(\d{2})$/.exec(packed);
        return `${m[1]}-${m[2]}-${m[3]}`;
    }

    function updateStatus(group) {
        dataStatusReport.updateByObject(group);
        shohousenPdfStatusReport.updateByObject(group);
        pharmaLetterPdfStatusReport.updateByObject(group);
        pharmaLabelPdfStatusReport.updateByObject(group);
        clinicLabelPdfStatusReport.updateByObject(group);
    }

    class Nav {
        constructor(ele) {
            this.ele = ele;
            this.tabPrevGroups = ele.find(".faxed-part-prev-groups").on("click", event => {
                this.showPrevGroups();
            });
        }

        setActive(e){
            this.ele.find(".nav-link").removeClass("active");
            e.addClass("active");
        }

        showPanel(panel){
            this.ele.find(".faxed-nav-content").addClass("d-none");
            panel.removeClass("d-none");
        }

        async showPrevGroups(){
            let content = $("#faxed-nav-content-prev-groups");
            let prevGroups = await getPrevGroups();
            let ul = $("<ul>");
            if (prevGroups) {
                prevGroups.forEach(group => {
                    let li = $("<li>");
                    let a = $("<a>").text(group).attr("href", "javascript:void(0)");
                    li.append(a);
                    a.on("click", async event => {
                        let [from, upto] = group.split("-");
                        from = packedToSqlDate(from);
                        upto = packedToSqlDate(upto);
                        let g = await getGroup(from, upto);
                        setFromValue(from);
                        setUptoValue(upto);
                        updateStatus(g);
                    });
                    ul.append(li);
                });
            }
            this.setActive(this.tabPrevGroups);
            content.html("").append(ul);
            this.showPanel(content);
        }
    }

    let nav = new Nav($(".nav"));

    async function setup() {
        let target = await guessNextTarget()
        setFromValue(target.from);
        setUptoValue(target.upto);
        updateStatus(target);
    }

    setup();
    nav.tabPrevGroups.click();

}
