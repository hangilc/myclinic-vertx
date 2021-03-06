let html = `
<h3>印刷設定</h3>

<div>
    <button type="button" class="btn btn-secondary"
        id="printer-setting-new-setting-button">新規印刷設定</button>
    <button type="button" class="btn btn-secondary"
        id="printer-setting-select-setting-button">印刷設定選択</button>
</div>

<div class="d-none mt-3" id="printer-setting-current-wrapper">
    印刷設定名：<span class="x-name"></span>
    <button type="button" class="x-modify-setting btn btn-success ml-3">再設定</button>
    <button type="button" class="x-print-reference-frame btn btn-success ml-2">基準印刷</button>
    <textarea class="x-textarea form-control mt-2" cols="30" rows="8"></textarea>
    <div class="mt-2">
        <button type="button" class="x-save-json-setting btn btn-success">保存</button>
        <button type="button" class="x-end btn btn-secondary">終了</button>
    </div>
</div>

<template id="printer-setting-select-setting-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">印刷設定選択</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <select class="form-control mt-2 form-control x-select" size="5"></select>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary x-enter">選択</button>
                    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
                </div>
            </div>
        </div>
    </div>
</template>
`;

export function getHtml(){
    return html;
}

export async function initPrinterSetting(){
    let {parseElement} = await import("../js/parse-element.js");
    let {SelectSettingDialog} = await import("./select-setting-dialog.js");
    let {CurrentSetting} = await import("./current-setting.js");

    $("#printer-setting-new-setting-button").on("click", async event => {
        let name = prompt("新規印刷設定の名前");
        let savedNames = await rest.listPrinterSetting();
        if( savedNames.includes(name) ){
            alert(`${name} はすでに存在します。`);
            return;
        }
        await rest.createPrinterSetting(name);
    });

    class SelectSettingDialogFactory {
        create(settingList){
            let html = $("#printer-setting-select-setting-dialog-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let dialog = new SelectSettingDialog(ele, map, rest);
            dialog.init();
            dialog.set(settingList);
            return dialog;
        }
    }

    let selectSettingDialogFactory = new SelectSettingDialogFactory();

    let currentSetting = (function(){
        let ele = $("#printer-setting-current-wrapper");
        let map = parseElement(ele);
        let comp = new CurrentSetting(ele, map, rest);
        comp.init();
        comp.set();
        comp.onJsonSettingUpdated(async () => {
            let name = comp.getName();
            if( name ){
                let jsonSetting = await rest.getPrinterJsonSetting(name);
                comp.set(name, jsonSetting);
            }
        });
        comp.onEnd(() => {
            comp.set();
        });
        return comp;
    })();

    $("#printer-setting-select-setting-button").on("click", async event => {
        let settingList = await rest.listPrinterSetting();
        let dialog = selectSettingDialogFactory.create(settingList);
        let result = await dialog.open();
        if( result ){
            let jsonSetting = await rest.getPrinterJsonSetting(result);
            currentSetting.set(result, jsonSetting);
        }
    });

}
