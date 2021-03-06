let html = `
<h3>紹介状</h3>

<div>
    <button type="button" class="btn btn-secondary" id="refer-select-patient-button">患者選択</button>
    <button type="button" class="btn btn-link" id="refer-end-patient-button">患者終了</button>
</div>

<div class="mt-3 row">
    <div class="col-lg-7">
        <div id="refer-current-wrapper"></div>
    </div>
    <div class="col-lg-5" id="refer-record-list-wrapper"></div>
</div>

<template id="refer-patient-select-dialog-template">
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

<template id="refer-current-template" class="mt-3">
    <div>
        <div>
            患者：（<span class="x-patient-id"></span>）<span class="x-name"></span>
        </div>
        <div class="mt-2">
            <div class="form row">
                <div class="col-sm-3 d-flex justify-content-end col-form-label">表題</div>
                <div class="col-sm-9 form-row x-refer-title-controls">
                    <div class="form-check form-check-inline">
                        <input type="radio" name="refer-title" value="紹介状"
                               class="form-check-inline" checked>
                        <label class="form-check-label">紹介状</label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input type="radio" name="refer-title" value="ご報告"
                               class="form-check-inline">
                        <label class="form-check-label">ご報告</label>
                    </div>
                </div>
            </div>
            <div class="form row">
                <div class="col-sm-3 d-flex justify-content-end col-form-label">紹介病院</div>
                <div class="col-sm-8">
                    <input type="text" class="x-refer-hospital form-control"/>
                </div>
                <div class="col-sm-1 pl-0 dropleft">
                    <button type="button" class="btn btn-link ml-0 pl-0 dropdown-toggle"
                        data-toggle="dropdown">選択</button>
                    <div class="dropdown-menu x-suggest-dropdown-items">
                        <a href="javascript:void(0)" class="dropdown-item">河北病院</a>
                    </div>
                </div>
            </div>
            <div class="form row mt-2">
                <div class="col-sm-3 d-flex justify-content-end col-form-label">紹介先生</div>
                <div class="col-sm-6">
                    <input type="text" class="x-refer-doctor form-control"/>
                </div>
                <div class="col-sm-3 col-form-label">先生御机下</div>
            </div>
            <div class="form row mt-2">
                <div class="col-sm-3 d-flex justify-content-end col-form-label">診断</div>
                <div class="col-sm-9">
                    <input type="text" class="x-diagnosis form-control"/>
                </div>
            </div>
            <div class="form row mt-2">
                <div class="col-sm-3 d-flex justify-content-end col-form-label">発行日</div>
                <div class="col-sm-9">
                    <input type="text" class="x-issue-date form-control"/>
                </div>
            </div>
        </div>
        <div>
            <textarea class="form-control mt-2 x-content" rows="16"></textarea>
        </div>
        <div class="x-saved-pdf-workarea-wrapper"></div>
        <div class="mt-2">
            <button type="button" class="btn btn-success x-create">作成</button>
            <button type="button" class="btn btn-success x-save ml-2">保存</button>
        </div>
        <div class="mt-3 x-prev_">
            <h5>履歴</h5>
            <table class="table">
                <thead>
                <tr>
                    <th>作成日</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody></tbody>
            </table>

        </div>
    </div>
</template>
`;

export function getHtml(){
    return html;
}

export     async function initRefer(){
    let {PatientSelectDialog} = await import("./patient-select-dialog.js");
    let {parseElement} = await import("../js/parse-element.js");
    let {Current} = await import("./current.js");
    let {recordListFactory} = await import("./record-list/record-list.js");

    let currentPatient = null;

    let referList = await rest.getReferList();

    class PatientSearchDialogFactory {
        create(){
            let html = $("#refer-patient-select-dialog-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let dialog = new PatientSelectDialog(ele, map, rest);
            dialog.init();
            dialog.set();
            return dialog;
        }
    }

    let patientSelectDialogFactory = new PatientSearchDialogFactory();

    class CurrentFactory {
        create(patient, prevs, referList){
            let html = $("#refer-current-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let comp = new Current(ele, map, rest);
            comp.init(referList);
            comp.set(patient, prevs);
            return comp;
        }
    }

    let currentFactory = new CurrentFactory();
    let currentComponent = null;

    $("#refer-select-patient-button").on("click", async event => {
        let dialog = patientSelectDialogFactory.create();
        let result = await dialog.open();
        if( result ){
            currentPatient = result;
            let prevs = await rest.listRefer(currentPatient.patientId);
            let current = currentFactory.create(currentPatient, prevs, referList);
            current.prependTo($("#refer-current-wrapper").html(""));
            let recordList = recordListFactory.create(currentPatient.patientId, rest);
            await recordList.update(1);
            recordList.appendTo($("#refer-record-list-wrapper").html(""));
            currentComponent = current;
        }
    });

    $("#refer-end-patient-button").on("click", event => {
        if( currentComponent ){
            if( currentComponent.getContent() ){
                if( !confirm("この患者を終了していいですか？") ){
                    return;
                }
            }
            currentPatient = null;
            currentComponent = null;
        }
        $("#refer-current-wrapper").html("");
        $("#refer-record-list-wrapper").html("");
    });
}
