let html = `
<h3>主治医意見書</h3>

<div>
    <button type="button" class="btn btn-secondary" id="shujii-select-patient-button">患者選択</button>
    <button type="button" class="btn btn-secondary ml-2" id="shujii-save-button">保存</button>
    <button type="button" class="btn btn-secondary ml-2" id="shujii-end-patient-button">患者終了</button>
    <a href="../json/shujii-criteria" target="_blank" class="ml-2">判定基準</a>
</div>

<div class="mt-2 row">
    <div class="col-lg-7">
        <textarea class="form-control" id="shujii-master-textarea" rows="20"></textarea>
        <h4 class="mt-3">作成</h4>
        <textarea class="form-control" id="shujii-detail-textarea" rows="8"></textarea>
        <div class="mt-2">
            <button type="button" class="btn btn-success" id="shujii-print-button">印刷</button>
            （印刷時５行までが望ましい）
        </div>
        <div class="mt-2">
            <form id="shujii-upload-form">
                <input type="file" name="file" class="x-file" multiple/>
                <button type="submit" class="x-upload-button btn btn-success">画像アップロード</button>
            </form>
        </div>
    </div>
    <div class="col-lg-5">
        <div id="shujii-nav-wrapper"></div>
        <div id="shujii-record-wrapper" class="mt-2"></div>
    </div>
</div>

<template id="shujii-patient-select-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">患者選択</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="x-search_">
                        <form class="form-inline x-form">
                            <input type="text" class="form-control x-search-text"/>
                            <button type="submit" class="btn btn-secondary ml-2">検索</button>
                            <button type="submit" class="btn btn-secondary ml-2 x-prev">既出患者</button>
                        </form>
                        <select size="10" class="x-select form-control mt-2"></select>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="d-flex justify-content-end">
                        <button type="button" class="btn btn-secondary x-enter">選択</button>
                        <button type="button" class="btn btn-secondary x-cancel ml-2">キャンセル</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<template id="shujii-nav-template">
    <div class="d-none mt-2">
        <a href="javascript:void(0)" class="x-first">最初</a>
        <a href="javascript:void(0)" class="x-prev ml-1">前へ</a>
        <a href="javascript:void(0)" class="x-next ml-1">次へ</a>
        <a href="javascript:void(0)" class="x-last ml-1 mr-1">最後</a>
        [<span class="x-page"></span>/<span class="x-total"></span>]
    </div>
</template>

<template id="shujii-record-template">
    <div class="mb-3">
        <div class="x-title font-weight-bold py-1 px-2" style="background-color: #eee"></div>
        <div class="x-text-wrapper"></div>
        <div class="x-drug-wrapper">
            <div class="d-none x-drug-prep">Rp）</div>
        </div>
        <div class="x-conduct-wrapper"></div>
    </div>
</template>

<template id="shujii-drug-template">
    <div><span class="x-index"></span>）<span class="x-rep"></span></div>
</template>
`;

export function getHtml(){
    console.log("shujii template", html);
    return html;
}

export async function initShujii(pane, rest, printAPI, reloadFunc) {
    let {parseElement} = await import("../js/parse-element.js");
    let {PatientSelectDialog} = await import("./patient-select-dialog.js");
    let {Nav} = await import("./nav.js");
    let {Record} = await import("./record.js");
    let {Title} = await import("./title.js");
    let {Text} = await import("./text.js");
    let {Drug} = await import("./drug.js");
    let kanjidate = await import("../js/kanjidate.js");
    let currentPatient = null;

    class PatientSelectDialogFactory {
        create() {
            let html = $("template#shujii-patient-select-dialog-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let dialog = new PatientSelectDialog(ele, map, rest);
            dialog.init();
            dialog.set();
            return dialog;
        }
    }

    let patientSelectDialogFactory = new PatientSelectDialogFactory();

    class NavFactory {
        constructor() {
            this.html = $("template#shujii-nav-template").html();
        }

        create() {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new Nav(ele, map, rest);
            comp.init();
            return comp;
        }
    }

    let navFactory = new NavFactory();
    let nav = navFactory.create();
    nav.onChange(page => loadPage(page));
    nav.appendTo($("#shujii-nav-wrapper"));

    function setNav(page, total) {
        nav.set(page, total);
        if (total > 1) {
            nav.show();
        } else {
            nav.hide();
        }
    }

    class TitleFactory {
        create(visit){
            let ele = $("<div>");
            let map = parseElement(ele);
            let comp = new Title(ele, map, rest);
            comp.init();
            comp.set(visit);
            return comp;
        }
    }

    class TextFactory {
        create(text){
            let ele = $("<div>");
            let map = parseElement(ele);
            let comp = new Text(ele, map, rest);
            comp.init();
            comp.set(text);
            return comp;
        }
    }

    class DrugFactory {
        create(index, drugFull){
            let html = $("template#shujii-drug-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let comp = new Drug(ele, map, rest);
            comp.init();
            comp.set(index, drugFull);
            return comp;
        }
    }

    class RecordFactory {
        constructor(){
            this.html = $("template#shujii-record-template").html();
            this.titleFactory = new TitleFactory();
            this.textFactory = new TextFactory();
            this.drugFactory = new DrugFactory();
        }

        create(visitFull){
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new Record(ele, map, rest);
            comp.init(this.titleFactory, this.textFactory, this.drugFactory);
            comp.set(visitFull);
            return comp;
        }
    }

    let recordFactory = new RecordFactory();

    function setRecords(visitFulls, scrollToBottom=true){
        let wrapper = $("#shujii-record-wrapper");
        wrapper.html("");
        let lastRecord = null;
        for(let visitFull of visitFulls){
            let record = recordFactory.create(visitFull);
            record.appendTo(wrapper);
            lastRecord = record;
        }
        if( scrollToBottom ) {
            lastRecord.ele[0].scrollIntoView();
        }
    }

    async function loadPage(page, scrollToBottom=true){
        let visitPage = await rest.listVisit(currentPatient.patientId, page - 1);
        setNav(visitPage.page + 1, visitPage.totalPages);
        setRecords(visitPage.visits, scrollToBottom);
    }

    $("#shujii-select-patient-button").on("click", async event => {
        let dialog = patientSelectDialogFactory.create();
        let result = await dialog.open();
        if (!result) {
            return;
        }
        currentPatient = result;
        let masterText = await rest.getShujiiMasterText(currentPatient);
        if( !masterText ){
            let p = currentPatient;
            masterText = `(${p.patientId})${p.lastName}${p.firstName}`;
            masterText += "\n\n";
        }
        let textarea = $("#shujii-master-textarea");
        textarea.val(masterText);
        await loadPage(1, false);
    });

    $("#shujii-save-button").on("click", async event => {
        if( currentPatient ){
            let name = currentPatient.lastName + currentPatient.firstName;
            let textarea = $("#shujii-master-textarea");
            let text = textarea.val();
            await rest.saveShujiiMasterText(name, currentPatient.patientId, text);
        }
    });

    $("#shujii-end-patient-button").on("click", event => {
        if( !confirm("この患者を終了していいですか？") ){
            return;
        }
        reloadFunc();
    });

    $("#shujii-print-button").on("click", async event => {
        let detail = $("#shujii-detail-textarea").val();
        let clinicInfo = await rest.getClinicInfo();
        let data = {
            doctorName: clinicInfo.doctorName,
            clinicName: clinicInfo.name,
            clinicAddress: clinicInfo.address,
            phone: clinicInfo.tel,
            fax: clinicInfo.fax,
            detail: detail
        };
        let ops = await rest.compileShujiiDrawer(data);
        //let setting = "shujii";
        let setting = null;
        //await rest.printDrawer([ops], setting);
        printAPI.print([], [ops], setting);
    });

    (function(){
        function fileParts(filename){
            let i = filename.indexOf(".");
            return [filename.substring(0, i), filename.substring(i+1)]
        }

        function getExtension(filename){
            let i = filename.indexOf(".");
            return filename.substring(i);
        }

        let form = $("#shujii-upload-form");
        let map = parseElement(form);
        let fileElement = map.file;
        form.on("submit", event => {
            if( currentPatient ){
                let patientName = currentPatient.lastName + currentPatient.firstName;
                let files = fileElement.get(0).files;
                let formData = new FormData();
                formData.append("name", patientName);
                formData.append("patient-id", currentPatient.patientId);
                let date = kanjidate.todayAsSqldate();
                let index = 1;
                for(let file of files){
                    let fname = `${patientName}-${date}(${index})` +
                        getExtension(file.name);
                    formData.append(`file${index}`, file, fname);
                    index += 1;
                }
                $.ajax("/json/save-shujii-image", {
                    data: formData,
                    processData: false,
                    contentType: false,
                    method: "POST"
                });
            }
            return false;
        });
    })();

}
