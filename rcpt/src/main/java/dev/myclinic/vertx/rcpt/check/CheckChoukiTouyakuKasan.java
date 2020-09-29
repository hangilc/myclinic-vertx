package dev.myclinic.vertx.rcpt.check;

import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedShinryouMap;

class CheckChoukiTouyakuKasan extends CheckBase {

    //private static Logger logger = LoggerFactory.getLogger(CheckChoukiTouyakuKasan.class);
    private ResolvedShinryouMap sm;

    CheckChoukiTouyakuKasan(Scope scope) {
        super(scope);
        sm = getShinryouMaster();
    }

    void check() {
        int nChoukiTouyaku = countChoukiTouyakuKasan();
        int nTokuteiKasan = countTokuteiShohouKasan();
        if( nChoukiTouyaku > 0 ){
            if( nTokuteiKasan > 0 ){
                String em = String.format("特定疾患処方管理加算(%d件)を削除します。", nTokuteiKasan);
                error("特定疾患処方管理加算請求不可", em, () ->
                        removeExtraShinryouMasterInVisits(sm.特定疾患処方, 0)
                );
            }
            if( nChoukiTouyaku > 1 ){
                String em = String.format("長期投薬加算(%d件中%d件)を削除します。",
                        nChoukiTouyaku, nChoukiTouyaku - 1);
                error("長期投薬加算重複", em, () ->
                    removeExtraShinryouMasterInVisits(sm.長期処方, 1)
                );
            }
        } else {
            if( nTokuteiKasan > 2 ){
                String em = String.format("特定疾患処方管理加算(%d件中%d件)を削除します。",
                        nTokuteiKasan, nTokuteiKasan - 2);
                error("特定疾患処方管理加算３回以上", em, () ->
                        removeExtraShinryouMasterInVisits(sm.特定疾患処方, 2)
                );
            }
        }
    }

    private int countChoukiTouyakuKasan(){
        return countShinryouMasterInVisits(sm.長期処方);
    }

    private int countTokuteiShohouKasan(){
        return countShinryouMasterInVisits(sm.特定疾患処方);
    }

}
