package dev.myclinic.vertx.rcpt.check;

import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedDiseaseAdjMap;
import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedDiseaseMap;
import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedShinryouMap;

import java.time.LocalDate;

class CheckByoumei {

    //private static Logger logger = LoggerFactory.getLogger(CheckByoumei.class);
    private Scope scope;

    CheckByoumei(Scope scope) {
        this.scope = scope;
    }

    void check(){
        ResolvedShinryouMap shinryouMap = scope.resolvedMasterMap.shinryouMap;
        ResolvedDiseaseMap byoumeiMap = scope.resolvedMasterMap.diseaseMap;
        ResolvedDiseaseAdjMap adjMap = scope.resolvedMasterMap.diseaseAdjMap;
        scope.visits.forEach(visit -> {
            LocalDate visitDate = scope.getVisitDate(visit);
            visit.shinryouList.forEach(shinryou -> {
                int shinryoucode = shinryou.shinryou.shinryoucode;
                if( shinryoucode == shinryouMap.ＨｂＡ１ｃ ){
                    if( !scope.hasByoumeiAt(byoumeiMap.糖尿病, visitDate) ){
                        scope.error(
                                "「ＨｂＡ１ｃ」に対する病名がありません。",
                                "病名「糖尿病の疑い」を追加します。",
                                () -> scope.enterDisease(visit, byoumeiMap.糖尿病, adjMap.疑い)
                        );
                    }
                } else if( shinryoucode == shinryouMap.ＰＳＡ ){
                    if( !scope.hasByoumeiAt(byoumeiMap.前立腺癌, visitDate) ){
                        scope.error(
                                "「ＰＳＡ」に対する病名がありません。",
                                "病名「前立腺癌の疑い」を追加します。",
                                () -> scope.enterDisease(visit, byoumeiMap.前立腺癌, adjMap.疑い)
                        );
                    }
                }
            });
        });
    }

}
