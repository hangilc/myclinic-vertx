import {show, hide} from "../../js/dom-helper.js";
import {PatientManip} from "./patient-manip.js";

let html = `
<div class="pane practice">
    <div class="row" id="practice-top">
        <div class="col-xl-9">
            <div class="row">
                <h3 class="col-xl-2">診察</h3>
                <div class="col-xl-10 form-inline">
                    <div class="dropdown" id="practice-select-patient-menu">
                        <button class="btn btn-secondary dropdown-toggle" type="button"
                                data-toggle="dropdown" aria-haspopup="true"
                                aria-expanded="false">
                            患者選択
                        </button>
                        <div class="dropdown-menu x-menu" aria-labelledby="dropdownMenuButton">
                            <a href="javascript:void(0)" class="x-wqueue dropdown-item mx-2">受付患者選択</a>
                            <a href="javascript:void(0)" class="x-search dropdown-item mx-2">患者検索</a>
                            <a href="javascript:void(0)" class="x-recent dropdown-item mx-2">最近の診察</a>
                            <a href="javascript:void(0)" class="x-today dropdown-item mx-2">本日の診察</a>
                            <a href="javascript:void(0)" class="x-prev dropdown-item mx-2">以前の診察</a>
                        </div>
                    </div>
                    <a href="javascript:void(0)" class="ml-2" id="practice-registered-drug-link">登録薬剤</a>
                    <a href="javascript:void(0)" class="ml-2" id="practice-search-text-globally">全文検索</a>
                </div>
            </div>
            <div id="practice-patient-info" class="session-listener mx-2 my-2"></div>
            <div id="practice-patient-manip" class="session-listener d-none mx-2 mb-2 form-inline"></div>
            <div id="practice-patient-manip-workarea"></div>
            <div class="practice-nav record-page-listener session-listener d-none mt-2"></div>
            <div id="practice-record-wrapper" class="record-page-listener session-listener"></div>
            <div class="practice-nav record-page-listener session-listener d-none mt-2"></div>
        </div>
        <div class="col-xl-3">
            <div id="practice-right-bar">
                <div id="practice-disease-wrapper" class="mb-3"></div>
                <div id="practice-general-workarea"></div>
            </div>
        </div>
    </div>
</div>

<template id="practice-patient-search-dialog-template">
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
                            <input class="form-control x-input"/>
                            <button type="submit" class="form-control ml-2">検索</button>
                        </form>
                        <select class="form-control mt-2 form-control x-select" size="5"></select>
                    </div>
                    <div class="card mt-2">
                        <div class="card-body">
                            <div class="row x-disp_">
                                <div class="col-sm-3">患者番号</div>
                                <div class="col-sm-9 x-patient-id"></div>
                                <div class="col-sm-3">氏名</div>
                                <div class="col-sm-9">
                                    <span class="x-last-name"></span><span
                                        class="x-first-name ml-2"></span>
                                </div>
                                <div class="col-sm-3">よみ</div>
                                <div class="col-sm-9 x-yomi">
                                    <span class="x-last-name-yomi"></span><span
                                        class="x-first-name-yomi ml-2"></span>
                                </div>
                                <div class="col-sm-3">生年月日</div>
                                <div class="col-sm-9 x-birthday"></div>
                                <div class="col-sm-3">性別</div>
                                <div class="col-sm-9 x-sex"></div>
                                <div class="col-sm-3">住所</div>
                                <div class="col-sm-9 x-address"></div>
                                <div class="col-sm-3">電話</div>
                                <div class="col-sm-9 x-phone"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary x-register-enter">受付・診察</button>
                    <button type="button" class="btn btn-primary x-enter">選択</button>
                    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
                </div>
            </div>
        </div>
    </div>
</template>

<template id="practice-select-wqueue-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">受付患者選択</h5>
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

<template id="practice-select-recent-visit-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">最近の診察</h5>
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

<template id="practice-select-todays-visit-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">本日の診察</h5>
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

<template id="practice-select-visit-at-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">以前の診察</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="form-inline">
                        <span>診察日</span>
                        <input type="date" class="x-date ml-1"/>
                    </div>
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

<template id="practice-shinryou-regular-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">診療行為入力</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="row x-checks">
                        <div class="col-sm-6">
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input" value="初診"
                                       id="practice-shinryou-regular-初診"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-初診">
                                    初診
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input" value="再診"
                                       id="practice-shinryou-regular-再診"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-再診">
                                    再診
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input" value="外来管理加算"
                                       id="practice-shinryou-regular-外来管理加算"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-外来管理加算">
                                    外来管理加算
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input" value="特定疾患管理"
                                       id="practice-shinryou-regular-特定疾患管理"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-特定疾患管理">
                                    特定疾患管理
                                </label>
                            </div>
                            <div class="form-check mt-3">
                                <input type="checkbox" class="form-check-input" value="尿便検査判断料"
                                       id="practice-shinryou-regular-尿便検査判断料"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-尿便検査判断料">
                                    尿便検査判断料
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input" value="血液検査判断料"
                                       id="practice-shinryou-regular-血液検査判断料"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-血液検査判断料">
                                    血液検査判断料
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input" value="生化Ⅰ判断料"
                                       id="practice-shinryou-regular-生化Ⅰ判断料"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-生化Ⅰ判断料">
                                    生化Ⅰ判断料
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input" value="生化Ⅱ判断料"
                                       id="practice-shinryou-regular-生化Ⅱ判断料"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-生化Ⅱ判断料">
                                    生化Ⅱ判断料
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input" value="免疫検査判断料"
                                       id="practice-shinryou-regular-免疫検査判断料"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-免疫検査判断料">
                                    免疫検査判断料
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input" value="微生物検査判断料"
                                       id="practice-shinryou-regular-微生物検査判断料"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-微生物検査判断料">
                                    微生物検査判断料
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input" value="静脈採血"
                                       id="practice-shinryou-regular-静脈採血"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-静脈採血">
                                    静脈採血
                                </label>
                            </div>
                        </div>
                        <div class="col-sm-6">
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="尿一般"
                                       id="practice-shinryou-regular-尿一般"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-尿一般">
                                    尿一般
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="便潜血"
                                       id="practice-shinryou-regular-便潜血"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-便潜血">
                                    便潜血
                                </label>
                            </div>
                            <div class="form-check mt-3">
                                <input type="checkbox" class="form-check-input"
                                       value="処方箋料"
                                       id="practice-shinryou-regular-処方箋料"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-処方箋料">
                                    処方箋料
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="特定疾患処方管理加算２（処方箋料）"
                                       id="practice-shinryou-regular-特定疾患処方管理加算２（処方箋料）"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-特定疾患処方管理加算２（処方箋料）">
                                    特定疾患処方管理加算２（処方箋料）
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="一般名処方加算２（処方箋料）"
                                       id="practice-shinryou-regular-一般名処方加算２（処方箋料）"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-一般名処方加算２（処方箋料）">
                                    一般名処方加算２（処方箋料）
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="一般名処方加算１（処方箋料）"
                                       id="practice-shinryou-regular-一般名処方加算１（処方箋料）"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-一般名処方加算１（処方箋料）">
                                    一般名処方加算１（処方箋料）
                                </label>
                            </div>
                            <div class="form-check mt-3">
                                <input type="checkbox" class="form-check-input"
                                       value="処方料"
                                       id="practice-shinryou-regular-処方料"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-処方料">
                                    処方料
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="処方料７"
                                       id="practice-shinryou-regular-処方料７"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-処方料７">
                                    処方料７
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="手帳記載加算"
                                       id="practice-shinryou-regular-手帳記載加算"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-手帳記載加算">
                                    手帳記載加算
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="特定疾患処方"
                                       id="practice-shinryou-regular-特定疾患処方"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-特定疾患処方">
                                    特定疾患処方
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="長期処方"
                                       id="practice-shinryou-regular-長期処方"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-長期処方">
                                    長期処方
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="内服調剤"
                                       id="practice-shinryou-regular-内服調剤"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-内服調剤">
                                    内服調剤
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="外用調剤"
                                       id="practice-shinryou-regular-外用調剤"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-外用調剤">
                                    外用調剤
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="調基"
                                       id="practice-shinryou-regular-調基"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-調基">
                                    調基
                                </label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="薬剤情報提供"
                                       id="practice-shinryou-regular-薬剤情報提供"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-薬剤情報提供">
                                    薬剤情報提供
                                </label>
                            </div>
                        </div>
                        <div class="col-sm-12 form-inline">
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       value="向精神薬"
                                       id="practice-shinryou-regular-向精神薬"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-向精神薬">
                                    向精神薬
                                </label>
                            </div>
                            <div class="form-check ml-2">
                                <input type="checkbox" class="form-check-input"
                                       value="心電図"
                                       id="practice-shinryou-regular-心電図"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-心電図">
                                    心電図
                                </label>
                            </div>
                            <div class="form-check ml-2">
                                <input type="checkbox" class="form-check-input"
                                       value="骨塩定量"
                                       id="practice-shinryou-regular-骨塩定量"/>
                                <label class="form-check-label"
                                       for="practice-shinryou-regular-骨塩定量">
                                    骨塩定量
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary x-enter">入力</button>
                    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
                </div>
            </div>
        </div>
    </div>
</template>

<template id="practice-title-template">
    <div class="mt-2 practice-title form-inline">
        <div class="x-text"></div>
        <div class="dropdown ml-auto">
            <button class="btn btn-link dropdown-toggle" type="button"
                    data-toggle="dropdown" aria-haspopup="true"
                    aria-expanded="false">
                操作
            </button>
            <div class="dropdown-menu x-menu_" aria-labelledby="dropdownMenuButton">
                <a href="javascript:void(0)" class="x-delete dropdown-item">この診察を削除</a>
                <a href="javascript:void(0)" class="x-temp-visit dropdown-item">暫定診察に設定</a>
                <a href="javascript:void(0)" class="x-untemp-visit dropdown-item">暫定診察の解除</a>
                <a href="javascript:void(0)" class="x-meisai dropdown-item">診療明細</a>
                <a href="javascript:void(0)" class="x-futan-wari-override dropdown-item">負担割オーバーライド</a>
            </div>
        </div>
    </div>
</template>

<template id="practice-hoken-template">
    <div></div>
</template>

<template id="practice-hoken-select-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">保険選択</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body x-body"></div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary x-enter">入力</button>
                    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
                </div>
            </div>
        </div>
    </div>
    <template class="x-item-template">
        <div class="form-check form-check">
            <input type="checkbox" class="form-check-input x-input">
            <div class="form-check-label x-label"></div>
        </div>
    </template>
</template>

<!--<template id="practice-text-template">-->
<!--    <div class="my-1 record-text"></div>-->
<!--</template>-->

<!--<template id="practice-text-disp-template">-->
<!--    <div class="my-1"></div>-->
<!--</template>-->

<template id="practice-enter-text-template">
    <div class="mt-2">
        <textarea class="form-control x-textarea" rows="6"></textarea>
        <div class="form-inline mt-2">
            <a href="javascript:void(0)" class="x-enter">入力</a>
            <a href="javascript:void(0)" class="x-cancel ml-2">キャンセル</a>
        </div>
    </div>
</template>

<template id="practice-edit-text-template">
    <div class="mt-2">
        <textarea class="form-control x-textarea" rows="6"></textarea>
        <div class="form-inline mt-2">
            <a href="javascript:void(0)" class="x-enter">入力</a>
            <a href="javascript:void(0)" class="x-cancel ml-2">キャンセル</a>
            <a href="javascript:void(0)" class="x-copy-memo d-none ml-2">引継ぎコピー</a>
            <a href="javascript:void(0)" class="x-delete ml-2">削除</a>
            <div class="dropbox x-shohousen-menu d-none">
                <button type="button" class="btn btn-link dropdown-toggle"
                        data-toggle="dropdown">処方箋</button>
                <div class="dropdown-menu">
                    <a href="javascript:void(0)" class="x-shohousen dropdown-item">処方箋発行</a>
                    <a href="javascript:void(0)" class="x-shohousen-fax dropdown-item">処方箋FAX</a>
                    <a href="javascript:void(0)" class="x-registered-presc dropdown-item">登録薬剤</a>
                    <a href="javascript:void(0)" class="x-format-presc dropdown-item">処方箋整形</a>
                    <a href="javascript:void(0)" class="x-preview-current dropdown-item">編集中表示</a>
                </div>
            </div>
            <a href="javascript:void(0)" class="x-copy ml-2">コピー</a>
        </div>
    </div>
</template>

<template id="practice-shohousen-preview-dialog-template">
    <div class="modal" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">処方箋</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="x-disp" style="width:148mm;height:210mm"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary x-print">印刷</button>
                    <button type="button" class="btn btn-secondary x-close">閉じる</button>
                </div>
            </div>
        </div>
    </div>
</template>

<template id="practice-shinryou-template">
    <div class="practice-shinryou"></div>
</template>

<template id="practice-shinryou-disp-template">
    <div></div>
</template>

<template id="practice-shinryou-edit-template">
    <div class="border border-secondary rounded p-2 my-2">
        <div>名称：<span class="x-label"></span></div>
        <div class="x-tekiyou"></div>
        <div class="mt-2">
            <button class="btn btn-secondary x-delete">削除</button>
            <button class="btn btn-secondary x-close">閉じる</button>
            <button class="btn btn-link x-edit-tekiyou">適用編集</button>
        </div>
    </div>
</template>

<template id="practice-conduct-disp-template">
    <div>
        <div class="x-kind"></div>
        <div class="x-gazou-label"></div>
        <div class="x-shinryou"></div>
        <div class="x-drug"></div>
        <div class="x-kizai"></div>
    </div>
</template>

<template id="practice-drug-disp-template">
    <div><span class="x-index"></span>）<span class="x-rep"></span></div>
</template>

<template id="practice-meisai-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">会計</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="x-detail_">
                        <pre class="x-items"></pre>
                        <div class="x-summary"></div>
                        <div class="mb-2 x-value-wrapper">
                            <span class="x-value"></span> 
                            <a href="javascript:void(0)" class="x-modify-charge-button">変更</a>
                        </div>
                        <div class="d-none form-inline x-modify-charge-workarea">
                            <input type="text" class="form-control mr-2 x-modify-charge-input"/>
                            <span class="mr-2">円</span>
                            <button class="btn btn-primary btn-sm mr-2 x-modify-charge-enter">入力</button>
                            <button class="btn btn-secondary btn-sm x-modify-charge-cancel">キャンセル</button>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary x-enter">入力</button>
                    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
                </div>
            </div>
        </div>
    </div>
</template>

<template id="practice-send-fax-template">
    <div class="border founded mb-3 p-2">
        <div class="mt-2">
            <span class="x-pdf-file"></span>
            <a href="javascript:void(0)" class="x-view">プレビュー</a>
        </div>
        <div class="x-fax-number"></div>
        <div class="mt-4">
            <button class="btn btn-secondary x-send">送信</button>
            <a href="javascript:void(0)" class="x-cancel ml-2">キャンセル</a>
        </div>
    </div>
</template>

<template id="practice-fax-progress-template">
    <div class="border founded mb-3 p-2">
        <div class="x-title"></div>
        <div class="mt-2">
            <span class="x-pdf-file"></span>
            <a href="javascript:void(0)" class="x-view">表示</a>
        </div>
        <div class="x-fax-number"></div>
        <div class="x-message mt-4"></div>
        <div class="mt-4">
            <button class="btn btn-secondary x-re-send">再送信</button>
            <a href="javascript:void(0)" class="x-close ml-2">閉じる</a>
        </div>
    </div>
</template>

<template id="practice-charge-template">
    <div class="mt-2"></div>
</template>

<template id="practice-charge-disp-template">
    <div></div>
</template>

<template id="practice-charge-modify-template">
    <div class="mb-3 border border-secondary rounded p-2">
        <div class="d-flex p-2 mb-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">請求額の変更</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div>
            <div class="row">
                <div class="col-sm-5 d-flex justify-content-end">診療報酬総点</div>
                <div class="col-sm-7"><span class="x-total-ten mr-1"></span>点</div>
                <div class="col-sm-5 d-flex justify-content-end">負担割</div>
                <div class="col-sm-7"><span class="x-futan-wari mr-1"></span>割</div>
                <div class="col-sm-5 d-flex justify-content-end">現在の請求額</div>
                <div class="col-sm-7"><span class="x-current-charge mr-1"></span>円</div>
            </div>
            <div class="form-group row">
                <div class="col-sm-5 col-form-label d-flex justify-content-end">変更後請求額</div>
                <div class="col-sm-7 form-inline">
                    <input type="text" class="form-control x-charge mr-1" size="6"/>円
                </div>
            </div>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button class="btn btn-secondary x-enter">入力</button>
            <button class="btn btn-secondary x-cancel ml-2">キャンセル</button>
        </div>
    </div>
</template>

<template id="practice-nav-template">
    <div class="d-none mt-2">
        <a href="javascript:void(0)" class="x-first">最初</a>
        <a href="javascript:void(0)" class="x-prev ml-1">前へ</a>
        <a href="javascript:void(0)" class="x-next ml-1">次へ</a>
        <a href="javascript:void(0)" class="x-last ml-1 mr-1">最後</a>
        [<span class="x-page"></span>/<span class="x-total"></span>]
    </div>
</template>

<template id="practice-disease-area-template">
    <div class="d-none">
        <h5>病名</h5>
        <div class="x-workarea"></div>
        <div class="x-commands mt-2">
            <a href="javascript:void(0)" class="x-current">現行</a>
            <a href="javascript:void(0)" class="x-add">追加</a>
            <a href="javascript:void(0)" class="x-end">転機</a>
            <a href="javascript:void(0)" class="x-edit">編集</a>
        </div>
    </div>
</template>

<template id="practice-disease-add-template">
    <div>
        <div>
            名称：<span class="x-name"></span>
        </div>
        <div class="mt-1">
            <input type="date" class="x-date-input form-control"/>
        </div>
        <div class="mt-1">
            <button type="button" class="x-enter btn btn-secondary">入力</button>
            <a href="javascript:void(0)" class="x-susp">の疑い</a>
            <a href="javascript:void(0)" class="x-del-adj">修飾語削除</a>
        </div>
        <form class="x-search-form mt-1">
            <div class="form-inline">
                <input type="text" class="x-search-text form-control"/>
                <button type="submit" class="btn btn-secondary mt-1">検索</button>
                <a href="javascript:void(0)" class="x-example mt-1 ml-2">例</a>
            </div>
            <div class="mt-1" onsubmit="return false;">
                <input type="radio" name="search-kind" class="x-disease-radio" checked> 病名
                <input type="radio" name="search-kind" class="x-adj-radio"> 修飾語
            </div>
        </form>
        <div class="mt-1">
            <select size="10" class="x-select form-control"></select>
        </div>
    </div>
</template>

<template id="practice-disease-end-template">
    <div>
        <div class="x-list"></div>
        <div>
            <input type="date" class="x-date-input form-control"/>
            <div class="x-date-commands_">
                <a href="javascript:void(0)" class="x-advance-week">週</a>
                <a href="javascript:void(0)" class="x-today">今日</a>
                <a href="javascript:void(0)" class="x-end-of-month">月末</a>
                <a href="javascript:void(0)" class="x-end-of-last-month">先月末</a>
            </div>
        </div>
        <div>
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
            <button type="button" class="x-enter btn btn-secondary">入力</button>
        </div>
    </div>
</template>

<template id="practice-disease-edit-template">
    <div>
        <div class="x-panel_">
            <div>名称：<span class="x-name"></span></div>
            <div>開始日：<span class="x-start-date"></span></div>
            <div>転機：<span class="x-end-reason"></span></div>
            <div>終了日：<span class="x-end-date"></span></div>
        </div>
        <div class="mt-1">
            <button type="button" class="x-edit btn btn-secondary">編集</button>
        </div>
        <select class="form-control x-select mt-1" size="6"></select>
    </div>
</template>

<template id="practice-disease-modify-template">
    <div>
        <div>
            名前：<span class="x-name"></span>
        </div>
        <div>
            <input type="date" class="x-start-date form-control"/>
        </div>
        <div>から</div>
        <div>
            <input type="date" class="x-end-date form-control"/>
        </div>
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
</template>

<template id="practice-search-text-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title x-title"></h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form class="x-search-form form-inline">
                        <input type="text" class="x-search-text form-control"/>
                        <button type="submit" class="btn btn-primary ml-2">検索</button>
                    </form>
                    <div class="x-nav_ d-none mt-2"></div>
                    <div class="x-result"></div>
                </div>
            </div>
        </div>
        <template class="x-item-template">
            <div class="my-2 border border-secondary rounded p-2">
                <div class="x-title_ bg-light font-weight-bold p-1">
                    <div class="x-text"></div>
                </div>
                <div class="x-text_ mt-2">
                    <div></div>
                </div>
            </div>
        </template>
        <template class="x-global-item-template">
            <div class="my-2 border border-secondary rounded p-2">
                <div class="x-title_ bg-light font-weight-bold p-1">
                    <span class="x-patient text-primary"></span> <span class="x-text ml-2"></span>
                </div>
                <div class="x-text_ mt-2">
                    <div></div>
                </div>
            </div>
        </template>
    </div>
</template>

<template id="practice-example-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Title</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body"></div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary x-close">閉じる</button>
                </div>
            </div>
        </div>
    </div>
</template>

<template id="practice-visit-meisai-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">診療明細</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="x-sections"></div>
                    <div>総点：<span class="x-total-ten"></span></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary x-close">閉じる</button>
                </div>
            </div>
        </div>
    </div>
    <template class="x-item-template">
        <div>
            <div class="x-title"></div>
            <div class="x-detail"></div>
        </div>
    </template>
    <template class="x-detail-template">
        <div class="row">
            <div class="col-sm-2"></div>
            <div class="col-sm-4 x-detail-label"></div>
            <div class="col-sm-2 x-detail-ten"></div>
        </div>
    </template>
</template>

<template id="practice-widget-example-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2 mb-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1"></div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div></div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
        </div>
    </div>
</template>
`;

export function getHtml() {
    return html;
}

export async function initLayout(pane, rest, controller, printAPI) {
    let {parseElement} = await import("./parse-element.js");
    let {PatientDisplay} = await import("./patient-display.js");
    let {wqueueStateCodeToRep} = await import("../js/consts.js");
    let {SelectWqueueDialog} = await import("./select-wqueue-dialog.js");
    let {SelectRecentVisitDialog} = await import("./select-recent-visit-dialog.js");
    let {SelectTodaysVisitDialog} = await import("./select-todays-visit-dialog.js");
    let {SelectPreviousVisitDialog} = await import("./select-prev-visit-dialog.js");
    let {ShinryouRegularDialog} = await import("./shinryou-regular-dialog.js");
    let {ShinryouDisp} = await import("./shinryou-disp.js");
    let {Shinryou} = await import("./shinryou.js");
    let {ShinryouEdit} = await import("./shinryou-edit.js");
    let {Text} = await import("./text/text.js");
    let {TextDisp} = await import("./text/text-disp.js");
    let {TextEnter} = await import("./text/text-enter.js");
    let {TextEdit} = await import("./text/text-edit.js");
    let {Record} = await import("./record.js");
    let {Hoken} = await import("./hoken.js");
    let {HokenDisp} = await import("./hoken-disp.js");
    let {HokenSelectDialog} = await import("./hoken-select-dialog.js");
    let {ConductDisp} = await import("./conduct-disp.js");
    let {DrugDisp} = await import("./drug-disp.js");
    let {MeisaiDialog} = await import("./meisai-dialog.js");
    let {SendFax} = await import("./send-fax.js");
    let {FaxProgress} = await import("./fax-progress.js");
    let {Nav} = await import("./nav.js");
    let {DiseaseArea} = await import("./disease-area.js");
    let {DiseaseCurrent} = await import("./disease-current.js");
    let {DiseaseAdd, initDiseaseExamples} = await import("./disease-add.js");
    let {DiseaseEnd} = await import("./disease-end.js");
    let {DiseaseEdit} = await import("./disease-edit.js");
    let {DiseaseModify} = await import("./disease-modify.js");
    let {SearchTextForPatientDialog} = await import("./search-text-for-patient-dialog.js");
    let {SearchTextGloballyDialog} = await import("./search-text-globally-dialog.js");
    let {PatientSearchDialog} = await import("./patient-search-dialog.js");
    let {RegisteredDrugDialog} = await import("./registered-drug-dialog/registered-drug-dialog.js")
    let {UploadImageDialog} = await import("./upload-image-dialog.js");
    let {UploadProgress} = await import("./upload-progress.js");
    let {PatientImageList} = await import("../../components/patient-image-list.js");
    let {NoPayList} = await import("./no-pay-list.js");

    let prop = {
        rest,
        printAPI,
        patient: null,
        currentVisitId: 0,
        tempVisitId: 0,
        getTargetVisitId(){
            if( this.currentVisitId > 0 ){
                return this.currentVisitId;
            } else {
                return this.tempVisitId;
            }
        }
    };

    pane.addEventListener("start-session", async event => {
        let patientId = event.detail.patientId;
        let visitId = event.detail.visitId;
        prop.patient = await prop.rest.getPatient(patientId);
        prop.currentVisitId = visitId;
        prop.tempVisitId = 0;
        pane.querySelectorAll(".session-listener").forEach(e =>
            e.dispatchEvent(new Event("session-started")));
        pane.dispatchEvent(new CustomEvent("load-record-page", {detail: 0}));
    });

    pane.addEventListener("end-session", async event => {
        prop.patient = null;
        prop.currentVisitId = 0;
        prop.tempVisitId = 0;
        pane.querySelectorAll(".session-listener").forEach(e =>
            e.dispatchEvent(new Event("session-ended")));
    });

    pane.addEventListener("change-current-visit-id", event => {
        let visitId = event.detail;
        console.log("change-current-visit-id");
    });

    pane.addEventListener("change-temp-visit-id", event => {
        let visitId = event.detail;
        console.log("change-temp-visit-id");
    });

    pane.addEventListener("load-record-page", async event => {
        let page = event.detail;
        let recordPage = await prop.rest.listVisit(prop.patient.patientId, page);
        pane.querySelectorAll(".record-page-listener").forEach(e => e.dispatchEvent(
            new CustomEvent("record-page-loaded", {detail: recordPage})
        ));
    });

    pane.addEventListener("set-temp-visit", event => {
        let visitId = event.detail;
        if( prop.currentVisitId !== 0 ){
            alert("現在診察中なので、暫定診察を設定できません。");
            return;
        }
        prop.tempVisitId = visitId;
        pane.querySelectorAll(".temp-visit-listener").forEach(e => {
            e.dispatchEvent(new Event("temp-visit-changed"));
        });
    });

    pane.addEventListener("clear-temp-visit", event => {
        let visitId = event.detail;
        let save = prop.tempVisitId;
        prop.tempVisitId = 0;
        pane.querySelectorAll(".temp-visit-listener").forEach(e => {
            e.dispatchEvent(new Event("temp-visit-changed"));
        });
    });

    pane.addEventListener("text-copied", event => {
        let newText = event.detail;
        let visitId = newText.visitId;
        let e = getRecordElementByVisitId(visitId);
        if( e ){
            e.dispatchEvent(new CustomEvent("text-entered", {detail: newText}));
        }
    });

    // function postStartSession(patientId, visitId) {
    //     pane.dispatchEvent(new CustomEvent("start-session",
    //         {bubbles: true, detail: {patientId, visitId}}));
    // }
    //
    // function postChangeVisitId(visitId) {
    //     pane.dispatchEvent(new CustomEvent("change-visit-id", {bubles: true, detail: visitId}));
    // }
    //
    // function postChangeTempVisitId(visitId) {
    //     pane.dispatchEvent(new CustomEvent("change-temp-visit-id", {bubles: true, detail: visitId}));
    // }
    //
    // function addPatientChangedListener(f) {
    //     pane.addEventListener("patient-changed", event => f(event.detail));
    // }
    //
    // function addRecordsChangedListener(f) {
    //     pane.addEventListener("records-changed", event =>
    //         f(event.detail.records, event.detail.page, event.detail.totalPages));
    // }
    //
    // function addVisitIdChangedListener(f) {
    //     pane.addEventListener("visit-id-changed", event => f(event.detail));
    // }
    //
    // function addTempVisitIdChangedListener(f) {
    //     pane.addEventListener("temp-visit-id-changed", event => f(event.detail));
    // }
    //
    // class CurrentVisitManager {
    //     resolveCopyTarget() {
    //         return controller.getVisitId() || controller.getTempVisitId();
    //     }
    //
    //     getCurrentVisitId(){
    //         return controller.getVisitId();
    //     }
    //
    //     getTempVisitId(){
    //         return controller.getTempVisitId();
    //     }
    // }
    //
    // let currentVisitManager = new CurrentVisitManager();

    function getTemplateHtml(templateId) {
        let html = $("template#" + templateId).html();
        if (!html) {
            console.error(`cannot find "${templateId}"`);
        }
        return html;
    }

    let noPayList = null;

    pane.addEventListener("add-to-no-pay-list", async event => {
        let visitId = event.detail;
        if (noPayList == null) {
            noPayList = new NoPayList(prop);
            noPayList.ele.addEventListener("closed", event => noPayList = null);
            document.getElementById("practice-general-workarea").append(noPayList.ele);
        }
        await noPayList.add(visitId);
    });

    pane.addEventListener("payment-updated", async event => {
        let visitIds = event.detail;
        console.log("payment-updated", visitIds);
        await batchUpdatePaymentState(visitIds);
    });

    class PatientSearchDialogFactory {
        create() {
            let html = $("template#practice-patient-search-dialog-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let dialog = new PatientSearchDialog(ele, map, rest);
            dialog.init();
            return dialog;
        }
    }

    let selectWqueueDialog = (function () {
        let html = getTemplateHtml("practice-select-wqueue-dialog-template");
        let ele = $(html);
        let map = parseElement(ele);
        let dialog = new SelectWqueueDialog(ele, map, rest);
        dialog.init(state => wqueueStateCodeToRep(state));
        return dialog;
    })();

    let selectRecentVisitDialog = (function () {
        let html = getTemplateHtml("practice-select-recent-visit-dialog-template");
        let ele = $(html);
        let map = parseElement(ele);
        let dialog = new SelectRecentVisitDialog(ele, map, rest);
        dialog.init();
        return dialog;
    })();

    let selectTodaysVisitDialog = (function () {
        let html = getTemplateHtml("practice-select-todays-visit-dialog-template");
        let ele = $(html);
        let map = parseElement(ele);
        let dialog = new SelectTodaysVisitDialog(ele, map, rest);
        dialog.init();
        return dialog;
    })();

    let selectPreviousVisitDialog = (function () {
        let html = getTemplateHtml("practice-select-visit-at-dialog-template");
        let ele = $(html);
        let map = parseElement(ele);
        let dialog = new SelectPreviousVisitDialog(ele, map, rest);
        dialog.init();
        return dialog;
    })();

    class SearchTextGloballyDialogFactory {
        constructor() {
            this.html = getTemplateHtml("practice-search-text-dialog-template");
        }

        create() {
            let ele = $(this.html);
            let map = parseElement(ele);
            let dialog = new SearchTextGloballyDialog(ele, map, rest);
            dialog.init("全文検索");
            dialog.set();
            return dialog;
        }
    }

    $("#practice-registered-drug-link").on("click", async _ => {
        let dialog = new RegisteredDrugDialog(rest);
        await dialog.open();
    });

    (function () {
        let searchTextGloballyDialogFactory = new SearchTextGloballyDialogFactory();
        $("#practice-search-text-globally").on("click", async event => {
            let dialog = searchTextGloballyDialogFactory.create();
            await dialog.open();
        });
    })();

    // class ShinryouRegularDialogFactory {
    //     constructor() {
    //         this.html = getTemplateHtml("practice-shinryou-regular-dialog-template");
    //     }
    //
    //     create(visitId) {
    //         let ele = $(this.html);
    //         let map = parseElement(ele);
    //         let dialog = new ShinryouRegularDialog(ele, map, rest);
    //         dialog.init(visitId);
    //         return dialog;
    //     }
    // }

    // class PatientInfo {
    //     constructor(ele, map) {
    //         this.ele = ele;
    //         this.map = map;
    //         this.detail = map.detail;
    //         map.detailLink.on("click", event => this.toggleDetail());
    //         this.display = new PatientDisplay(map);
    //     }
    //
    //     setPatient(patient) {
    //         this.display.setPatient(patient);
    //         if (patient) {
    //             this.ele.removeClass("d-none");
    //         } else {
    //             this.ele.addClass("d-none");
    //         }
    //     }
    //
    //     clear() {
    //         this.display.clear();
    //     }
    //
    //     show() {
    //         this.ele.removeClass("d-none");
    //     }
    //
    //     hide() {
    //         this.ele.addClass("d-none");
    //     }
    //
    //     showDetail() {
    //         this.detail.removeClass("d-none");
    //     }
    //
    //     hideDetail() {
    //         this.detail.addClass("d-none");
    //     }
    //
    //     toggleDetail() {
    //         if (this.detail.hasClass("d-none")) {
    //             this.showDetail();
    //         } else {
    //             this.hideDetail();
    //         }
    //     }
    // }

    document.getElementById("practice-patient-info").addEventListener("session-started", event => {
        let patient = prop.patient;
        let disp = new PatientDisplay(patient);
        let e = event.target;
        e.innerHTML = "";
        e.append(disp.ele);
    });

    document.getElementById("practice-patient-info").addEventListener("session-ended", event => {
        event.target.innerHTML = "";
    });

    class MeisaiDialogFactory {
        constructor() {
            this.html = getTemplateHtml("practice-meisai-dialog-template");
        }

        create(meisai) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let dialog = new MeisaiDialog(ele, map, rest);
            dialog.init(meisai);
            return dialog;
        }
    }

    class SearchTextForPatientDialogFactory {
        constructor() {
            this.html = getTemplateHtml("practice-search-text-dialog-template");
        }

        create(patientId) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let dialog = new SearchTextForPatientDialog(ele, map, rest);
            dialog.init("文章検索");
            dialog.set(patientId);
            return dialog;
        }
    }

    // addPatientChangedListener(patient => {
    //     document.getElementById("practice-patient-manip-workarea").innerHTML = "";
    // });

    new PatientManip(prop, document.getElementById("practice-patient-manip"));

    document.getElementById("practice-patient-manip").addEventListener("session-started", event => {
        show(event.target);
    });

    document.getElementById("practice-patient-manip").addEventListener("session-ended", event => {
        hide(event.target);
    });

    (function () {
        let ele = $("#practice-patient-manip");
        let map = parseElement(ele);
        let meisaiDialogFactory = new MeisaiDialogFactory();
        let searchTextForPatientDialogFactory = new SearchTextForPatientDialogFactory();

        // addPatientChangedListener(patient => {
        //     if (!patient) {
        //         ele.addClass("d-none");
        //     } else {
        //         ele.removeClass("d-none");
        //     }
        // })

        map.cashier.on("click", async event => {
            let visitId = controller.getVisitId();
            if (visitId <= 0) {
                alert("現在診察中ではないので、会計はできません。");
                return;
            }
            let meisai = await rest.getMeisai(visitId);
            let dialog = meisaiDialogFactory.create(meisai);
            let result = await dialog.open();
            if (result) {
                await rest.endExam(visitId, meisai.charge);
                await controller.endSession();
            }
        });

        map.end.on("click", async event => {
            let visitId = controller.getVisitId();
            if (visitId > 0) {
                await rest.suspendExam(visitId);
            }
            await controller.endSession();
        });

        map.registerCurrent.on("click", async event => {
            if (controller.getVisitId() === 0 && controller.getPatientId() > 0 &&
                confirm("この患者を診察登録しますか？")) {
                let patientId = controller.getPatientId();
                let visitId = await rest.startVisit(patientId);
                await controller.startSession(patientId, visitId);
            }
        });

        map.searchText.on("click", async event => {
            let patientId = controller.getPatientId();
            let dialog = searchTextForPatientDialogFactory.create(patientId);
            await dialog.open();
        });

        map.refer.on("click", event => {
            alert("Not implemented.");
        });

        map.uploadImage.on("click", async event => {
            let patientId = controller.getPatientId();
            if (patientId > 0) {
                let dialog = new UploadImageDialog(patientId);
                let uploaders = await dialog.open();
                if (uploaders) {
                    console.log("uploaders", uploaders);
                    let reporter = new UploadProgress(uploaders);
                    document.getElementById("practice-general-workarea").append(reporter.ele);
                }
            }
        });

        map.listImage.on("click", async event => {
            let patientId = controller.getPatientId();
            if (patientId > 0) {
                let w = new PatientImageList(rest, true);
                await w.init(patientId);
                let wrapper = document.getElementById("practice-patient-manip-workarea");
                wrapper.prepend(w.ele);
            }
        });

    })
    // ();

    class SendFaxFactory {
        constructor() {
            this.html = getTemplateHtml("practice-send-fax-template");
        }

        create(pdfFile, faxNumber) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new SendFax(ele, map, rest);
            comp.init(pdfFile, faxNumber);
            return comp;
        }
    }

    class FaxProgressFactory {
        constructor() {
            this.html = getTemplateHtml("practice-fax-progress-template");
        }

        create(patient, faxNumber, pdfFile, faxSid) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new FaxProgress(ele, map, rest);
            let patientName = patient.lastName + patient.firstName;
            comp.init(patientName, faxNumber, pdfFile, faxSid);
            return comp;
        }
    }

    class TextDispFactory {
        constructor() {
            this.html = getTemplateHtml("practice-text-disp-template");
        }

        create(text) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new TextDisp(ele, map, rest);
            comp.init();
            comp.set(text);
            return comp;
        }
    }

    class TextEnterFactory {
        constructor() {
            this.html = getTemplateHtml("practice-enter-text-template");
        }

        create(visitId) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new TextEnter(ele, map, rest);
            comp.init(visitId);
            return comp;
        }
    }

    class TextFactory {
        constructor() {
            this.html = $("template#practice-text-template").html();
            this.rest = rest;
            this.classToken = "practice-text";
            this.textDispFactory = new TextDispFactory();
            this.textEditFactory = new TextEditFactory();
        }

        create(text) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new Text(ele, map, this.rest);
            comp.init(text, this.textDispFactory, this.textEditFactory,
                this.classToken);
            return comp;
        }

        listTextComponents(wrapperElement) {
            let xs = wrapperElement.find("." + this.classToken);
            let components = [];
            for (let i = 0; i < xs.length; i++) {
                components.push(xs.slice(i, i + 1).data("component"));
            }
            return components;
        }
    }

    class HokenSelectDialogFactory {
        create(hokenEx, visitId, current) {
            let html = $("template#practice-hoken-select-dialog-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let dialog = new HokenSelectDialog(ele, map, rest);
            dialog.init(visitId);
            dialog.set(hokenEx, current);
            return dialog;
        }
    }

    class HokenDispFactory {
        create(hokenRep) {
            let html = "<div></div>";
            let ele = $(html);
            let map = parseElement(ele);
            let compDisp = new HokenDisp(ele, map, rest);
            compDisp.init();
            compDisp.set(hokenRep);
            return compDisp;
        }
    }

    class HokenFactory {
        constructor() {
            this.html = getTemplateHtml("practice-hoken-template");
            this.hokenDispFactory = new HokenDispFactory();
            this.hokenSelectDialogFactory = new HokenSelectDialogFactory();
        }

        create(patientId, date, visitId, hoken, hokenRep) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let compHoken = new Hoken(ele, map, rest);
            compHoken.init(patientId, date, visitId, this.hokenDispFactory, this.hokenSelectDialogFactory);
            compHoken.set(hoken, hokenRep);
            return compHoken;
        }
    }

    class DrugDispFactory {
        constructor() {
            this.html = getTemplateHtml("practice-drug-disp-template");
        }

        create(drugFull) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new DrugDisp(ele, map, rest);
            comp.init(drugFull);
            return comp;
        }
    }

    class ShinryouDispFactory {
        constructor() {
            this.html = getTemplateHtml("practice-shinryou-disp-template");
        }

        create(shinryouFull) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new ShinryouDisp(ele, map, rest);
            comp.init(shinryouFull);
            return comp;
        }
    }

    class ShinryouEditFactory {
        constructor() {
            this.html = getTemplateHtml("practice-shinryou-edit-template");
        }

        create(shinryouFull) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new ShinryouEdit(ele, map, rest);
            comp.init(shinryouFull);
            return comp;
        }
    }

    class ShinryouFactory {
        constructor() {
            this.html = getTemplateHtml("practice-shinryou-template");
            this.shinryouDispFactory = new ShinryouDispFactory();
            this.shinryouEditFactory = new ShinryouEditFactory();
        }

        create(shinryouFull) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new Shinryou(ele, map, rest);
            comp.init(shinryouFull, this.shinryouDispFactory, this.shinryouEditFactory);
            return comp;
        }
    }

    // class ShohousenPreviewDialogFactory {
    //     constructor() {
    //         this.html = getTemplateHtml("practice-shohousen-preview-dialog-template");
    //         this.drawerSvgModule = DrawerSvg;
    //     }
    //
    //     create(ops) {
    //         let ele = $(this.html);
    //         let map = parseElement(ele);
    //         let dialog = new ShohousenPreviewDialog(opt);
    //         dialog.init(ops, this.drawerSvgModule);
    //         return dialog;
    //     }
    // }

    class TextEditFactory {
        constructor() {
            this.html = getTemplateHtml("practice-edit-text-template");
            this.rest = rest;
            //this.currentVisitManager = currentVisitManager;
            //this.shohousenPreviewDialogFactory = new ShohousenPreviewDialogFactory();
        }

        create(text) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new TextEdit(ele, map, this.rest, prop.printAPI);
            comp.init(text, this.currentVisitManager
                //, this.shohousenPreviewDialogFactory
            );
            return comp;
        }
    }

    class ConductDispFactory {
        constructor() {
            this.html = getTemplateHtml("practice-conduct-disp-template");
        }

        create(conductFull) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new ConductDisp(ele, map, rest);
            comp.init(conductFull);
            return comp;
        }
    }

    function getRecordElementByVisitId(visitId){
        return pane.querySelector(`.practice-record[data-visit-id='${visitId}']`);
    }

    document.getElementById("practice-record-wrapper").addEventListener("record-page-loaded", async event => {
        let recordPage = event.detail;
        let wrapper = event.target;
        wrapper.innerHTML = "";
        for(let visitFull of recordPage.visits){
            let record = new Record(prop, visitFull);
            wrapper.append(record.ele);
        }
    });

    document.getElementById("practice-record-wrapper").addEventListener("session-ended", event => {
        event.target.innerHTML = "";
    });

    // class RecordFactory {
    //     constructor() {
    //         this.html = getTemplateHtml("practice-record-template");
    //         this.wrapper = $("#practice-record-wrapper");
    //         this.generalWorkarea = $("#practice-general-workarea");
    //         //this.titleFactory = new TitleFactory();
    //         this.textFactory = new TextFactory();
    //         this.textEnterFactory = new TextEnterFactory();
    //         this.hokenFactory = new HokenFactory();
    //         this.shinryouFactory = new ShinryouFactory();
    //         this.shinryouRegularDialogFactory = new ShinryouRegularDialogFactory();
    //         this.conductDispFactory = new ConductDispFactory();
    //         this.drugDispFactory = new DrugDispFactory();
    //         this.sendFaxFactory = new SendFaxFactory();
    //         this.faxProgressFactory = new FaxProgressFactory();
    //         //this.chargeFactory = new ChargeFactory();
    //         //this.currentVisitManager = currentVisitManager;
    //     }
    //
    //     create(visitFull, hokenRep) {
    //         let ele = $(this.html);
    //         let map = parseElement(ele);
    //         let record = new Record(prop, ele, map);
    //         record.init(visitFull, hokenRep,
    //             //this.titleFactory,
    //             this.textFactory,
    //             this.hokenFactory, this.shinryouFactory, this.textEnterFactory,
    //             this.shinryouRegularDialogFactory, this.conductDispFactory,
    //             this.drugDispFactory, this.sendFaxFactory,
    //             //this.chargeFactory,
    //             this.currentVisitManager);
    //         record.onDelete(async visitId => {
    //             await rest.deleteVisit(visitId);
    //             record.remove();
    //             postChangeVisitId(0);
    //         });
    //         record.onTempVisit(visitId => postChangeTempVisitId(visitId));
    //         record.onClearTempVisit(() => postChangeTempVisitId(0));
    //         record.onFaxSent(async (event, data) => {
    //             let patient = await rest.getPatient(visitFull.visit.patientId);
    //             let compProgress = this.faxProgressFactory.create(patient, data.faxNumber,
    //                 data.pdfFile, data.faxSid);
    //             compProgress.appendTo(this.generalWorkarea);
    //             compProgress.start();
    //         });
    //         record.onShinryouCopied((targetVisitId, shinryouFulls) => {
    //             let targetRec = findRecord(targetVisitId);
    //             if (targetRec) {
    //                 for (let shinryouFull of shinryouFulls) {
    //                     targetRec.addShinryou(shinryouFull);
    //                 }
    //             }
    //         });
    //         return record;
    //     }
    // }

    (function () {
        let map = parseElement($("#practice-select-patient-menu"));
        let patientSearchDialogFactory = new PatientSearchDialogFactory();

        // async function cancelSession(){
        //     let patientId = controller.getPatientId();
        //     if( patientId > 0 ){
        //         let visitId = controller.getVisitId();
        //         if( visitId > 0 ){
        //             await rest.suspendExam(visitId);
        //         }
        //         await controller.endSession();
        //     }
        // }

        map.wqueue.on("click", async event => {
            let result = await selectWqueueDialog.open();
            if (result && result.mode === "selected") {
                let wqueueFull = result.data;
                let visitId = wqueueFull.visit.visitId;
                let patientId = wqueueFull.patient.patientId;
                pane.dispatchEvent(new Event("end-session"));
                await rest.startExam(visitId);
                pane.dispatchEvent(new CustomEvent("start-session", {
                    detail: {
                        patientId, visitId
                    }
                }));
            }
        });

        map.search.on("click", async event => {
            let dialog = patientSearchDialogFactory.create();
            let result = await dialog.open();
            if (result && result.mode === "enter") {
                let patientId = result.patient.patientId;
                pane.dispatchEvent(new Event("end-session"));
                pane.dispatchEvent(new CustomEvent("start-session", {
                    detail: {
                        patientId,
                        visitId: 0
                    }
                }));
            } else if (result && result.mode === "register-enter") {
                let visitId = result.visitId;
                let patientId = result.patient.patientId;
                pane.dispatchEvent(new Event("end-session"));
                await rest.startExam(visitId);
                pane.dispatchEvent(new CustomEvent("start-session", {
                    detail: {
                        patientId, visitId
                    }
                }));
            }
        });

        map.recent.on("click", async event => {
            let result = await selectRecentVisitDialog.open();
            if (result.mode === "selected") {
                let patientId = result.data.patient.patientId;
                pane.dispatchEvent(new Event("end-session"));
                pane.dispatchEvent(new CustomEvent("start-session", {
                    detail: {
                        patientId,
                        visitId: 0
                    }
                }));

            }
        });

        map.today.on("click", async event => {
            let result = await selectTodaysVisitDialog.open();
            if (result.mode === "selected") {
                let patientId = result.data.patient.patientId;
                pane.dispatchEvent(new Event("end-session"));
                pane.dispatchEvent(new CustomEvent("start-session", {
                    detail: {
                        patientId,
                        visitId: 0
                    }
                }));

            }
        });

        map.prev.on("click", async event => {
            let result = await selectPreviousVisitDialog.open();
            if (result.mode === "selected") {
                let patientId = result.data.patient.patientId;
                pane.dispatchEvent(new Event("end-session"));
                pane.dispatchEvent(new CustomEvent("start-session", {
                    detail: {
                        patientId,
                        visitId: 0
                    }
                }));

            }
        });
    })();

    class DiseaseCurrentFactory {
        constructor() {
            this.html = "<div></div>";
        }

        create(diseaseFulls) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new DiseaseCurrent(ele, map, rest);
            comp.init();
            comp.set(diseaseFulls);
            return comp;
        }
    }

    class DiseaseAddFactory {
        constructor() {
            this.html = getTemplateHtml("practice-disease-add-template");
        }

        create(patientId, date) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new DiseaseAdd(ele, map, rest);
            comp.init();
            comp.set(patientId, date);
            return comp;
        }
    }

    class DiseaseEndFactory {
        constructor() {
            this.html = getTemplateHtml("practice-disease-end-template");
        }

        create(diseaseFulls) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new DiseaseEnd(ele, map, rest);
            comp.init();
            comp.set(diseaseFulls);
            return comp;
        }
    }

    class DiseaseEditFactory {
        constructor() {
            this.html = getTemplateHtml("practice-disease-edit-template");
        }

        create(diseaseFulls) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new DiseaseEdit(ele, map, rest);
            comp.init();
            comp.set(diseaseFulls);
            return comp;
        }
    }

    class DiseaseModifyFactory {
        constructor() {
            this.html = getTemplateHtml("practice-disease-modify-template");
        }

        create(diseaseFull, diseaseExamples) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let comp = new DiseaseModify(ele, map, rest);
            comp.init(diseaseExamples);
            comp.set(diseaseFull);
            return comp;
        }
    }

    let diseaseExamples = await rest.listDiseaseExample();
    initDiseaseExamples(diseaseExamples);

    // class DiseaseAreaFactory {
    //     constructor() {
    //         this.html = getTemplateHtml("practice-disease-area-template");
    //         this.diseaseCurrentFactory = new DiseaseCurrentFactory();
    //         this.diseaseAddFactory = new DiseaseAddFactory();
    //         this.diseaseEndFactory = new DiseaseEndFactory();
    //         this.diseaseEditFactory = new DiseaseEditFactory();
    //         this.diseaseModifyFactory = new DiseaseModifyFactory();
    //         this.diseaseExamples = diseaseExamples;
    //     }
    //
    //     create() {
    //         let ele = $(this.html);
    //         let map = parseElement(ele);
    //         let comp = new DiseaseArea(ele, map, rest);
    //         comp.init(this.diseaseCurrentFactory, this.diseaseAddFactory, this.diseaseEndFactory,
    //             this.diseaseEditFactory, this.diseaseModifyFactory, this.diseaseExamples);
    //         return comp;
    //     }
    // }

    // let diseaseArea = (function () {
    //     let diseaseAreaFactory = new DiseaseAreaFactory();
    //     let comp = diseaseAreaFactory.create();
    //     comp.appendTo($("#practice-disease-wrapper"));
    //     return comp;
    // })();

    // addPatientChangedListener(async patient => {
    //     if (!patient) {
    //         diseaseArea.set(0, null);
    //     } else {
    //         let patientId = patient.patientId;
    //         let cur = await rest.listCurrentDisease(patientId);
    //         diseaseArea.set(patientId, cur);
    //         diseaseArea.current();
    //     }
    //
    // });

    // let recordFactory = new RecordFactory();

    // class NavFactory {
    //     constructor() {
    //         this.html = getTemplateHtml("practice-nav-template");
    //     }
    //
    //     create() {
    //         let ele = $(this.html);
    //         let map = parseElement(ele);
    //         let comp = new Nav(ele, map, rest);
    //         comp.init();
    //         return comp;
    //     }
    //
    // }

    document.querySelectorAll(".practice-nav").forEach(e => {
        let nav = new Nav(e);
        e.addEventListener("record-page-loaded", event => {
            let page = event.detail;
            if( page.totalPages <= 1 ){
                hide(e);
            } else {
                nav.adaptToPage(page.page, page.totalPages);
                show(e);
            }
        });
        e.addEventListener("session-ended", event => hide(e));
    });

    function setRecords(visitFulls) {
        let recordWrapperElement = $("#practice-record-wrapper");
        recordWrapperElement.html("");
        let currentVisitId = controller.getVisitId();
        let tempVisitId = controller.getTempVisitId();
        for (let visitFull of visitFulls) {
            let record = recordFactory.create(visitFull, visitFull.hoken.rep);
            if (visitFull.visit.visitId === currentVisitId) {
                record.markAsCurrent();
            } else if (visitFull.visit.visitId === tempVisitId) {
                record.markAsTemp();
            }
            record.onTextCopied((event, copiedText) => {
                let targetRec = findRecord(copiedText.visitId);
                if (targetRec) {
                    targetRec.addText(copiedText);
                }
            });
            record.appendTo(recordWrapperElement);
        }
    }

    async function batchUpdatePaymentState(visitIds) {
        let map = await rest.batchGetLastPayment(visitIds);
        let wrapper = document.getElementById("practice-record-wrapper");
        for (let visitId of Object.keys(map)) {
            let e = wrapper.querySelector(`.record-${visitId}`);
            if (e) {
                e.dispatchEvent(new CustomEvent("update-payment", {detail: map[visitId]}));
            }
        }
    }

    let noPay0410cache = {
        patientId: 0,
        visitIds: []
    };

    async function batchUpdate0410NoPay() {
        let patientId = controller.getPatientId();
        let cache = noPay0410cache;
        if (patientId > 0 && cache.patientId !== patientId) {
            cache.patientId = patientId;
            cache.visitIds = await rest.list0410NoPay(patientId);
        }
        let wrapper = document.getElementById("practice-record-wrapper");
        for (let visitId of cache.visitIds) {
            let e = wrapper.querySelector(`.record-${visitId}`);
            if (e) {
                e.dispatchEvent(new Event("update-0410-no-pay"));
            }
        }
    }

    // addRecordsChangedListener((records, page, totalPages) => {
    //     setRecords(records);
    //     let visitIds = records.map(visitFull => visitFull.visit.visitId);
    //     batchUpdatePaymentState(visitIds);
    //     batchUpdate0410NoPay();
    //     setNavs(page, totalPages);
    // });

    function forEachRecord(f) {
        let xs = $(".practice-record");
        let len = xs.length;
        for (let i = 0; i < len; i++) {
            let x = xs.slice(i, i + 1);
            let c = x.data("component");
            f(c);
        }
    }

    function findRecord(visitId) {
        let xs = $(".practice-record");
        let len = xs.length;
        for (let i = 0; i < len; i++) {
            let x = xs.slice(i, i + 1);
            let c = x.data("component");
            if (c.getVisitId() === visitId) {
                return c;
            }
        }
        return null;
    }

    // addTempVisitIdChangedListener(tempVisitId => {
    //     forEachRecord(record => {
    //         if (record.getVisitId() === tempVisitId) {
    //             record.markAsTemp();
    //         } else {
    //             record.clearMark();
    //         }
    //     });
    // });

}
