let html = `
<h3>受付</h3>

<div id="reception-main-commands" class="pane">
    <button type="button" class="btn btn-secondary x-new-patient">新規患者</button>
    <button type="button" class="btn btn-secondary x-search-patient ml-2">患者検索</button>
    <button type="button" class="btn btn-link x-search-payment ml-2">会計検索</button>
    <button type="button" class="btn btn-link x-search-receipt-paper ml-2">領収書用紙</button>
</div>

<div id="reception-commands-second-row">
    <div class="form-inline mt-2">
        患者番号：<input type="text" class="form-control"/>
        <button class="btn btn-secondary ml-2">診療受付</button>
        <button class="btn btn-secondary ml-2">患者情報</button>
    </div>
</div>

<div id="reception-workarea" class="my-3"></div>

<table class="table mt-3" id="reception-wqueue-table">
    <thead>
    <tr>
        <th scope="col">状態</th>
        <th scope="col">ID</th>
        <th scope="col">氏名</th>
        <th scope="col">よみ</th>
        <th scope="col">性別</th>
        <th scope="col">生年月日</th>
        <th scope="col">年齢</th>
        <th scope="col"></th>
    </tr>
    </thead>
    <tbody></tbody>
    <template class="x-item-template">
        <tr>
            <td class="x-state align-middle"></td>
            <th scope="row" class="x-patient-id align-middle"></th>
            <td class="x-name align-middle"></td>
            <td class="x-yomi align-middle"></td>
            <td class="x-sex align-middle"></td>
            <td class="x-birthday align-middle"></td>
            <td class="x-age align-middle"></td>
            <td class="x-manip form-inline">
                <div class="dropdown" id="practice-select-patient-menu">
                    <button class="btn btn-link dropdown-toggle" type="button"
                            data-toggle="dropdown" aria-haspopup="true"
                            aria-expanded="false">
                        操作
                    </button>
                    <div class="dropdown-menu x-menu_" aria-labelledby="dropdownMenuButton">
                        <a href="javascript:void(0)" class="x-delete dropdown-item mx-2">削除</a>
                    </div>
                </div>
            </td>
        </tr>
    </template>
    <template class="x-cashier-button-template">
        <button type="button" class="btn btn-primary">会計</button>
    </template>
</table>

<div id="reception-bottom-commands" class="form-inline">
    <button type="button" class="btn btn-secondary x-refresh">更新</button>
</div>

<template id="reception-patient-search-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">患者検索</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="x-search_">
                        <form class="form-inline x-form">
                            <input type="text" class="form-control x-search-text"/>
                            <button type="submit" class="btn btn-secondary ml-2">検索</button>
                            <button type="submit" class="btn btn-secondary ml-2 x-recent">最近の登録</button>
                        </form>
                        <select size="10" class="x-select form-control mt-2"></select>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="row">
                        <div class="col-md-8">
                            <div class="row x-disp_">
                                <div class="col-sm-4 text-right">患者番号：</div>
                                <div class="col-sm-8 x-patient-id"></div>
                                <div class="col-sm-4 text-right">氏名：</div>
                                <div class="col-sm-8">
                                    <span class="x-last-name"></span>
                                    <span class="x-first-name"></span>
                                </div>
                                <div class="col-sm-4 text-right">よみ：</div>
                                <div class="col-sm-8">
                                    <span class="x-last-name-yomi"></span>
                                    <span class="x-first-name-yomi"></span>
                                </div>
                                <div class="col-sm-4 text-right">生年月日：</div>
                                <div class="col-sm-8 x-birthday"></div>
                                <div class="col-sm-4 text-right">性別：</div>
                                <div class="col-sm-8 x-sex"></div>
                                <div class="col-sm-4 text-right">住所：</div>
                                <div class="col-sm-8 x-address"></div>
                                <div class="col-sm-4 text-right">電話番号：</div>
                                <div class="col-sm-8 x-phone"></div>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <button type="button" class="btn btn-success x-register btn-block">診療受付</button>
                            <button type="button" class="btn btn-secondary btn-block x-edit">編集</button>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</template>

<template id="reception-patient-and-hoken-edit-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">患者情報編集</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="mt-2 row">
            <div class="col-md-4">
                <div class="row x-disp_">
                    <div class="col-sm-4">患者番号：</div>
                    <div class="col-sm-8 x-patient-id"></div>
                    <div class="col-sm-4">氏名：</div>
                    <div class="col-sm-8">
                        <span class="x-last-name"></span>
                        <span class="x-first-name"></span>
                    </div>
                    <div class="col-sm-4">よみ：</div>
                    <div class="col-sm-8">
                        <span class="x-last-name-yomi"></span>
                        <span class="x-first-name-yomi"></span>
                    </div>
                    <div class="col-sm-4">生年月日：</div>
                    <div class="col-sm-8 x-birthday"></div>
                    <div class="col-sm-4">性別：</div>
                    <div class="col-sm-8 x-sex"></div>
                    <div class="col-sm-4">住所：</div>
                    <div class="col-sm-8 x-address"></div>
                    <div class="col-sm-4">電話番号：</div>
                    <div class="col-sm-8 x-phone"></div>
                </div>
                <div class="mt-2">
                    <button type="button" class="x-edit-basic btn btn-secondary">編集</button>
                </div>
            </div>
            <div class="col-md-8">
                <div>
                    <table class="table x-hoken-list">
                        <thead>
                        <tr>
                            <th>種別</th>
                            <th>期限開始</th>
                            <th>期限終了</th>
                            <th>本人・家族</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
                <div>
                    <input type="checkbox" class="x-current-only" checked> 現在有効のみ
                </div>
                <div class="mt-2">
                    <button class="btn btn-secondary x-new-shahokokuho">新規社保国保</button>
                    <button class="ml-2 btn btn-secondary x-new-koukikourei">新規後期高齢</button>
                    <button class="ml-2 btn btn-secondary x-new-kouhi">新規公費負担</button>
                </div>
            </div>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-register btn btn-success">診療受付</button>
            <button type="button" class="x-upload-image btn btn-link ml-2">画像保存</button>
            <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
        </div>
        <div class="x-workarea mt-3"></div>
    </div>
</template>

<template id="reception-patient-edit-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">患者情報編集</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="x-form_ mt-2">
            <div class="row">
                <div class="col-sm-2 d-flex justify-content-end">患者番号</div>
                <div class="x-patient-id col-md-10"></div>
            </div>

            <div class="row mt-2">
                <div class="col-sm-2 d-flex justify-content-end">氏名</div>
                <div class="col-sm-10 form-inline">
                    <input type="text" class="x-last-name form-control"/>
                    <input type="text" class="x-first-name form-control ml-2"/>
                </div>
            </div>

            <div class="row mt-2">
                <div class="col-sm-2 d-flex justify-content-end mt-2">よみ</div>
                <div class="col-sm-10 form-inline">
                    <input type="text" class="x-last-name-yomi form-control"/>
                    <input type="text" class="x-first-name-yomi form-control ml-2"/>
                </div>
            </div>

            <div class="row mt-2">
                <div class="col-sm-2 d-flex justify-content-end mt-2">生年月日</div>
                <div class="col-sm-10 form-inline x-birthday_">
                    <select class="x-gengou form-control">
                        <option>令和</option>
                        <option>平成</option>
                        <option selected>昭和</option>
                        <option>大正</option>
                        <option>明治</option>
                    </select>
                    <input type="text" class="x-nen form-control ml-2" size="3"/> 年
                    <input type="text" class="x-month form-control ml-2" size="3"/> 月
                    <input type="text" class="x-day form-control ml-2" size="3"/> 日
                </div>
            </div>

            <div class="row mt-2">
                <div class="col-sm-2 d-flex justify-content-end mt-2">性別</div>
                <div class="col-sm-10">
                    <form class="form-inline x-sex" onsubmit="return false">
                        <input type="radio" name="sex" value="M"> 男
                        <input type="radio" name="sex" value="F" checked class="ml-2"> 女
                    </form>
                </div>
            </div>

            <div class="row mt-2">
                <div class="col-sm-2 d-flex justify-content-end mt-2">住所</div>
                <div class="col-sm-10 form-inline">
                    <input type="text" class="x-address form-control" size="50"/>
                </div>
            </div>

            <div class="row mt-2">
                <div class="col-sm-2 d-flex justify-content-end mt-2">電話番号</div>
                <div class="col-sm-10 form-inline">
                    <input type="text" class="x-phone form-control"/>
                </div>

            </div>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-enter btn btn-secondary">入力</button>
            <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
        </div>
    </div>

</template>

<template id="reception-shahokokuho-new-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">新規社保国保入力</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="x-form_ mt-4">
            <form>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">保険者番号</div>
                    <div class="col-sm-10 form-inline">
                        <input type="text" class="form-control x-hokensha-bangou"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">被保険者</div>
                    <div class="col-sm-10 form-inline">
                        記号：<input type="text" class="form-control x-hihokensha-kigou mr-3"/>
                        番号：<input type="text" class="form-control x-hihokensha-bangou"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">本人・家族</div>
                    <div class="col-sm-10 form-inline">
                        <div class="form-check form-check-inline">
                            <input type="radio" name="honnin" class="form-check-input" value="1" checked/>
                            <div class="form-check-label">本人</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" name="honnin" class="form-check-input" value="0"/>
                            <div class="form-check-label">家族</div>
                        </div>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">開始日</div>
                    <div class="col-sm-10 form-inline x-valid-from_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">終了日</div>
                    <div class="col-sm-10 form-inline x-valid-upto_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                        <a href="javascript:void(0)" class="x-clear-valid-upto ml-2">クリア</a>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">高齢</div>
                    <div class="col-sm-10 form-inline">
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" checked name="kourei" value="0">
                            <div class="form-check-label">高齢でない</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="kourei" value="1"/>
                            <div class="form-check-label">１割</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="kourei" value="2"/>
                            <div class="form-check-label">２割</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="kourei" value="3"/>
                            <div class="form-check-label">３割</div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-enter btn btn-secondary">入力</button>
            <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
        </div>
    </div>
</template>

<template id="reception-shahokokuho-edit-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">社保国保編集</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="x-form_ mt-4">
            <form>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">保険者番号</div>
                    <div class="col-sm-10 form-inline">
                        <input type="text" class="form-control x-hokensha-bangou"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">被保険者</div>
                    <div class="col-sm-10 form-inline">
                        記号：<input type="text" class="form-control x-hihokensha-kigou mr-3"/>
                        番号：<input type="text" class="form-control x-hihokensha-bangou"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">本人・家族</div>
                    <div class="col-sm-10 form-inline">
                        <div class="form-check form-check-inline">
                            <input type="radio" name="honnin" class="form-check-input" value="1" checked/>
                            <div class="form-check-label">本人</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" name="honnin" class="form-check-input" value="0"/>
                            <div class="form-check-label">家族</div>
                        </div>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">開始日</div>
                    <div class="col-sm-10 form-inline x-valid-from_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">終了日</div>
                    <div class="col-sm-10 form-inline x-valid-upto_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                        <a href="javascript:void(0)" class="x-clear-valid-upto ml-2">クリア</a>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">高齢</div>
                    <div class="col-sm-10 form-inline">
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" checked name="kourei" value="0">
                            <div class="form-check-label">高齢でない</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="kourei" value="1"/>
                            <div class="form-check-label">１割</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="kourei" value="2"/>
                            <div class="form-check-label">２割</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="kourei" value="3"/>
                            <div class="form-check-label">３割</div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-enter btn btn-secondary">入力</button>
            <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
        </div>
    </div>
</template>

<template id="reception-koukikourei-new-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">新規後期高齢入力</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="x-form_ mt-4">
            <form>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">保険者番号</div>
                    <div class="col-sm-10 form-inline">
                        <input type="text" class="form-control x-hokensha-bangou"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">被保険者番号</div>
                    <div class="col-sm-10 form-inline">
                        <input type="text" class="form-control x-hihokensha-bangou"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">開始日</div>
                    <div class="col-sm-10 form-inline x-valid-from_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">終了日</div>
                    <div class="col-sm-10 form-inline x-valid-upto_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                        <a href="javascript:void(0)" class="x-clear-valid-upto ml-2">クリア</a>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">負担割</div>
                    <div class="col-sm-10 form-inline">
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="futan-wari" value="1" checked/>
                            <div class="form-check-label">１割</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="futan-wari" value="2"/>
                            <div class="form-check-label">２割</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="futan-wari" value="3"/>
                            <div class="form-check-label">３割</div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-enter btn btn-secondary">入力</button>
            <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
        </div>
    </div>
</template>

<template id="reception-koukikourei-edit-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">後期高齢保険編集</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="x-form_ mt-4">
            <form>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">保険者番号</div>
                    <div class="col-sm-10 form-inline">
                        <input type="text" class="form-control x-hokensha-bangou"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">被保険者番号</div>
                    <div class="col-sm-10 form-inline">
                        <input type="text" class="form-control x-hihokensha-bangou"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">開始日</div>
                    <div class="col-sm-10 form-inline x-valid-from_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">終了日</div>
                    <div class="col-sm-10 form-inline x-valid-upto_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                        <a href="javascript:void(0)" class="x-clear-valid-upto ml-2">クリア</a>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">負担割</div>
                    <div class="col-sm-10 form-inline">
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="futan-wari" value="1" checked/>
                            <div class="form-check-label">１割</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="futan-wari" value="2"/>
                            <div class="form-check-label">２割</div>
                        </div>
                        <div class="form-check form-check-inline">
                            <input type="radio" class="form-check-input" name="futan-wari" value="3"/>
                            <div class="form-check-label">３割</div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-enter btn btn-secondary">入力</button>
            <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
        </div>
    </div>
</template>

<template id="reception-kouhi-new-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">新規公費負担入力</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="x-form_ mt-4">
            <form>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">負担者番号</div>
                    <div class="col-sm-10 form-inline">
                        <input type="text" class="form-control x-futansha"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">受給者番号</div>
                    <div class="col-sm-10 form-inline">
                        <input type="text" class="form-control x-jukyuusha"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">開始日</div>
                    <div class="col-sm-10 form-inline x-valid-from_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">終了日</div>
                    <div class="col-sm-10 form-inline x-valid-upto_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                        <a href="javascript:void(0)" class="x-clear-valid-upto ml-2">クリア</a>
                    </div>
                </div>
            </form>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-enter btn btn-secondary">入力</button>
            <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
        </div>
    </div>
</template>

<template id="reception-kouhi-edit-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">公費負担編集</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="x-form_ mt-4">
            <form>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">負担者番号</div>
                    <div class="col-sm-10 form-inline">
                        <input type="text" class="form-control x-futansha"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">受給者番号</div>
                    <div class="col-sm-10 form-inline">
                        <input type="text" class="form-control x-jukyuusha"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">開始日</div>
                    <div class="col-sm-10 form-inline x-valid-from_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2 col-form-label d-flex justify-content-end">終了日</div>
                    <div class="col-sm-10 form-inline x-valid-upto_">
                        <select class="x-gengou form-control">
                            <option selected>令和</option>
                            <option>平成</option>
                        </select>
                        <input type="text" class="x-nen form-control ml-2 mr-1" size="3"/>年
                        <input type="text" class="x-month form-control ml-2 mr-1" size="3"/> 月
                        <input type="text" class="x-day form-control ml-2 mr-1" size="3"/> 日
                        <a href="javascript:void(0)" class="x-clear-valid-upto ml-2">クリア</a>
                    </div>
                </div>
            </form>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-enter btn btn-secondary">入力</button>
            <button type="button" class="x-close btn btn-secondary ml-2">キャンセル</button>
        </div>
    </div>
</template>

<template id="reception-shahokokuho-disp-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2 mb-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">社保国保データ</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="row x-disp_">
            <div class="col-sm-2 d-flex justify-content-end">保険者番号</div>
            <div class="col-sm-10 x-hokensha-bangou"></div>
            <div class="col-sm-2 d-flex justify-content-end">被保険者記号</div>
            <div class="col-sm-10 x-hihokensha-kigou"></div>
            <div class="col-sm-2 d-flex justify-content-end">被保険者番号</div>
            <div class="col-sm-10 x-hihokensha-bangou"></div>
            <div class="col-sm-2 d-flex justify-content-end">本人・家族</div>
            <div class="col-sm-10 x-honnin"></div>
            <div class="col-sm-2 d-flex justify-content-end">開始日</div>
            <div class="col-sm-10 x-valid-from"></div>
            <div class="col-sm-2 d-flex justify-content-end">終了日</div>
            <div class="col-sm-10 x-valid-upto"></div>
            <div class="col-sm-2 d-flex justify-content-end">高齢</div>
            <div class="col-sm-10 x-kourei"></div>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
        </div>
    </div>
</template>

<template id="reception-koukikourei-disp-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2 mb-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">後期高齢保険データ</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="row x-disp_">
            <div class="col-sm-2 d-flex justify-content-end">保険者番号</div>
            <div class="col-sm-10 x-hokensha-bangou"></div>
            <div class="col-sm-2 d-flex justify-content-end">被保険者番号</div>
            <div class="col-sm-10 x-hihokensha-bangou"></div>
            <div class="col-sm-2 d-flex justify-content-end">開始日</div>
            <div class="col-sm-10 x-valid-from"></div>
            <div class="col-sm-2 d-flex justify-content-end">終了日</div>
            <div class="col-sm-10 x-valid-upto"></div>
            <div class="col-sm-2 d-flex justify-content-end">負担割</div>
            <div class="col-sm-10 x-futan-wari"></div>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
        </div>
    </div>
</template>

<template id="reception-roujin-disp-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2 mb-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">老人保険データ</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="row x-disp_">
            <div class="col-sm-2 d-flex justify-content-end">市町村番号</div>
            <div class="col-sm-10 x-shichouson"></div>
            <div class="col-sm-2 d-flex justify-content-end">受給者番号</div>
            <div class="col-sm-10 x-jukyuusha"></div>
            <div class="col-sm-2 d-flex justify-content-end">開始日</div>
            <div class="col-sm-10 x-valid-from"></div>
            <div class="col-sm-2 d-flex justify-content-end">終了日</div>
            <div class="col-sm-10 x-valid-upto"></div>
            <div class="col-sm-2 d-flex justify-content-end">負担割</div>
            <div class="col-sm-10 x-futan-wari"></div>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
        </div>
    </div>
</template>

<template id="reception-kouhi-disp-widget-template">
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2 mb-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1">公費負担データ</div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="row x-disp_">
            <div class="col-sm-2 d-flex justify-content-end">負担者番号</div>
            <div class="col-sm-10 x-futansha"></div>
            <div class="col-sm-2 d-flex justify-content-end">受給者番号</div>
            <div class="col-sm-10 x-jukyuusha"></div>
            <div class="col-sm-2 d-flex justify-content-end">開始日</div>
            <div class="col-sm-10 x-valid-from"></div>
            <div class="col-sm-2 d-flex justify-content-end">終了日</div>
            <div class="col-sm-10 x-valid-upto"></div>
        </div>
        <div class="mt-2 d-flex justify-content-end">
            <button type="button" class="x-close btn btn-secondary ml-2">閉じる</button>
        </div>
    </div>
</template>

<template id="reception-widget-template">
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

<template id="reception-cashier-dialog-template">
    <div class="modal x-dialog" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">会計</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="x-sections"></div>
                    <div class="x-summary"></div>
                    <div class="x-payments"></div>
                    <div class="font-weight-bold x-charge"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary x-print-receipt">領収書印刷</button>
                    <button type="button" class="btn btn-secondary x-end">会計終了</button>
                    <button type="button" class="btn btn-link x-cancel">キャンセル</button>
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
`;

export function getHtml(){
    return html;
}

export async function initReception(pane) {
    let {parseElement} = await import("../js/parse-element.js");
    let {PatientSearchDialog} = await import("./patient-search-dialog.js");
    let {PatientAndHokenEditWidget} = await import("./patient-and-hoken-edit-widget.js");
    let {PatientEditWidget} = await import("./patient-edit-widget.js");
    let {ShahokokuhoNewWidget} = await import("./shahokokuho-new-widget.js");
    let {KoukikoureiNewWidget} = await import("./koukikourei-new-widget.js");
    let {KouhiNewWidget} = await import("./kouhi-new-widget.js");
    let {ShahokokuhoDispWidget} = await import("./shahokokuho-disp-widget.js");
    let {KoukikoureiDispWidget} = await import("./koukikourei-disp-widget.js");
    let {RoujinDispWidget} = await import("./roujin-disp-widget.js");
    let {KouhiDispWidget} = await import("./kouhi-disp-widget.js");
    let {ShahokokuhoEditWidget} = await import("./shahokokuho-edit-widget.js");
    let {KoukikoureiEditWidget} = await import("./koukikourei-edit-widget.js");
    let {KouhiEditWidget} = await import("./kouhi-edit-widget.js");
    let {HokenHelper} = await import("./hoken-helper.js");
    let {WqueueTable} = await import("./wqueue-table.js");
    let {CashierDialog} = await import("./cashier-dialog.js");
    let {Broadcaster} = await import("./broadcaster.js");

    let receptionWorkarea = $("#reception-workarea");
    let broadcaster = new Broadcaster();

    pane.addEventListener("onreloaded", async event => await refreshWqueueTable());

    broadcaster.listen("visit-created", visitId => refreshWqueueTable());
    broadcaster.listen("visit-deleted", visitId => refreshWqueueTable());

    class PatientAndHokenWidgetFactory {
        create(patient, currentHokenList, patientEditWidgetFactory, shahokokuhoNewWidgetFactory,
               koukikoureiNewWidgetFactory, kouhiNewWidgetFactory, shahokokuhoDispWidgetFactory,
               koukikoureiDispWidgetFactory, roujinDispWidgetFactory, kouhiDispWidgetFactory,
               shahokokuhoEditWidgetFactory, koukikoureiEditWidgetFactory,
               kouhiEditWidgetFactory) {
            let html = $("template#reception-patient-and-hoken-edit-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new PatientAndHokenEditWidget(ele, map, rest);
            widget.init(patientEditWidgetFactory, shahokokuhoNewWidgetFactory, koukikoureiNewWidgetFactory,
                kouhiNewWidgetFactory, shahokokuhoDispWidgetFactory,
                koukikoureiDispWidgetFactory, roujinDispWidgetFactory, kouhiDispWidgetFactory,
                shahokokuhoEditWidgetFactory, koukikoureiEditWidgetFactory, kouhiEditWidgetFactory,
                broadcaster);
            widget.set(patient, currentHokenList);
            return widget;
        }
    }

    class PatientEditWidgetFactory {
        create(patient) {
            let html = $("template#reception-patient-edit-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new PatientEditWidget(ele, map, rest);
            widget.init();
            widget.set(patient);
            return widget;
        }
    }

    class ShahokokuhoNewWidgetFactory {
        create(patientId) {
            let html = $("template#reception-shahokokuho-new-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new ShahokokuhoNewWidget(ele, map, rest);
            widget.init(patientId);
            widget.set();
            return widget;
        }
    }

    class KoukikoureiNewWidgetFactory {
        create(patientId) {
            let html = $("template#reception-koukikourei-new-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new KoukikoureiNewWidget(ele, map, rest);
            widget.init(patientId);
            widget.set();
            return widget;
        }
    }

    class KouhiNewWidgetFactory {
        create(patientId) {
            let html = $("template#reception-kouhi-new-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new KouhiNewWidget(ele, map, rest);
            widget.init(patientId);
            widget.set();
            return widget;
        }
    }

    class ShahokokuhoDispWidgetFactory {
        create(shahokokuho) {
            let html = $("template#reception-shahokokuho-disp-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new ShahokokuhoDispWidget(ele, map, rest);
            widget.init();
            widget.set(shahokokuho);
            return widget;
        }
    }

    class KoukikoureiDispWidgetFactory {
        create(koukikourei) {
            let html = $("template#reception-koukikourei-disp-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new KoukikoureiDispWidget(ele, map, rest);
            widget.init();
            widget.set(koukikourei);
            return widget;
        }
    }

    class RoujinDispWidgetFactory {
        create(roujin) {
            let html = $("template#reception-roujin-disp-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new RoujinDispWidget(ele, map, rest);
            widget.init();
            widget.set(roujin);
            return widget;
        }
    }

    class KouhiDispWidgetFactory {
        create(kouhi) {
            let html = $("template#reception-kouhi-disp-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new KouhiDispWidget(ele, map, rest);
            widget.init();
            widget.set(kouhi);
            return widget;
        }
    }

    class ShahokokuhoEditWidgetFactory {
        create(shahokokuho) {
            let html = $("template#reception-shahokokuho-edit-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new ShahokokuhoEditWidget(ele, map, rest);
            widget.init();
            widget.set(shahokokuho);
            return widget;
        }
    }

    class KoukikoureiEditWidgetFactory {
        create(koukikourei) {
            let html = $("template#reception-koukikourei-edit-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new KoukikoureiEditWidget(ele, map, rest);
            widget.init();
            widget.set(koukikourei);
            return widget;
        }
    }

    class KouhiEditWidgetFactory {
        create(kouhi) {
            let html = $("template#reception-kouhi-edit-widget-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let widget = new KouhiEditWidget(ele, map, rest);
            widget.init();
            widget.set(kouhi);
            return widget;
        }
    }

    (function () {
        let ele = $("#reception-main-commands");
        let map = parseElement(ele);
        let hokenHelper = new HokenHelper(rest);
        let patientAndHokenWidgetFactory = new PatientAndHokenWidgetFactory();
        let patientEditWidgetFactory = new PatientEditWidgetFactory();
        let shahokokuhoNewWidgetFactory = new ShahokokuhoNewWidgetFactory();
        let koukikoureiNewWidgetFactory = new KoukikoureiNewWidgetFactory();
        let kouhiNewWidgetFactory = new KouhiNewWidgetFactory();
        let shahokokuhoDispWidgetFactory = new ShahokokuhoDispWidgetFactory();
        let koukikoureiDispWidgetFactory = new KoukikoureiDispWidgetFactory();
        let roujinDispWidgetFactory = new RoujinDispWidgetFactory();
        let kouhiDispWidgetFactory = new KouhiDispWidgetFactory();
        let shahokokuhoEditWidgetFactory = new ShahokokuhoEditWidgetFactory();
        let koukikoureiEditWidgetFactory = new KoukikoureiEditWidgetFactory();
        let kouhiEditWidgetFactory = new KouhiEditWidgetFactory();

        map.newPatient.on("click", event => {
            alert("new patient: not supported");
        });

        map.searchPatient.on("click", async event => {
            let html = $("template#reception-patient-search-dialog-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let dialog = new PatientSearchDialog(ele, map, rest);
            dialog.init(broadcaster);
            dialog.set();
            let result = await dialog.open();
            if (result && result.action === "edit") {
                let patient = result.patient;
                let hokenList = await hokenHelper.fetchAvailableHoken(patient.patientId,
                    kanjidate.todayAsSqldate());
                let editWidget = patientAndHokenWidgetFactory.create(patient, hokenList,
                    patientEditWidgetFactory, shahokokuhoNewWidgetFactory,
                    koukikoureiNewWidgetFactory, kouhiNewWidgetFactory,
                    shahokokuhoDispWidgetFactory, koukikoureiDispWidgetFactory,
                    roujinDispWidgetFactory, kouhiDispWidgetFactory,
                    shahokokuhoEditWidgetFactory, koukikoureiEditWidgetFactory,
                    kouhiEditWidgetFactory);
                editWidget.prependTo(receptionWorkarea);
            }
        });
    })();

    class CashierDialogFactory {
        create(meisai, visitId, chargeValue, payments){
            let html = $("template#reception-cashier-dialog-template").html();
            let ele = $(html);
            let map = parseElement(ele);
            let dialog = new CashierDialog(ele, map, rest);
            dialog.init();
            dialog.set(meisai, visitId, chargeValue, payments);
            return dialog;
        }
    }

    class WqueueTableFactory {
        constructor() {
            this.cashierDialogFactory = new CashierDialogFactory();
        }
        create(){
            let ele = $("#reception-wqueue-table");
            let map = parseElement(ele);
            let comp = new WqueueTable(ele, map, rest);
            comp.init(this.cashierDialogFactory, broadcaster);
            comp.set();
            return comp;
        }
    }

    let wqueueTable = (new WqueueTableFactory()).create();
    wqueueTable.onChanged(() => refreshWqueueTable());

    async function refreshWqueueTable(){
        let wqueueFulls = await rest.listWqueueFull();
        wqueueTable.set(wqueueFulls);
    }

    $("#reception-main-commands").on("pane_shown", event => refreshWqueueTable());

    $("#reception-bottom-commands .x-refresh").on("click", event => {
        refreshWqueueTable();
    });

    await refreshWqueueTable();
}