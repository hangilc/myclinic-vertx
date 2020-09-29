package dev.myclinic.vertx.rcpt.check;

import dev.myclinic.vertx.dto.DrugFullDTO;

import java.util.List;
import java.util.function.Predicate;

class CheckBaseDrugShinryou extends CheckBase {

    //private static Logger logger = LoggerFactory.getLogger(CheckBaseDrugShinryou.class);
    private Predicate<DrugFullDTO> predicate;
    private int shinryoucode;
    private String shinryouName;

    CheckBaseDrugShinryou(Scope scope) {
        super(scope);
    }

    void setPredicate(Predicate<DrugFullDTO> predicate){
        this.predicate = predicate;
    }

    void setShinryoucode(int shinryoucode){
        this.shinryoucode = shinryoucode;
    }

    void setShinryouName(String shinryouName){
        this.shinryouName = shinryouName;
    }

    void check() {
        forEachVisit(visit -> {
            List<DrugFullDTO> ds = filterDrug(visit, predicate);
            int nDrugs = countShinryouMaster(visit, shinryoucode);
            if (ds.size() > 0) {
                if (nDrugs == 0) {
                    String em = shinryouName + "を追加します。";
                    error(shinryouName + "抜け", em, () -> enterShinryou(visit, shinryoucode));
                } else if (nDrugs > 1) {
                    String em = messageForRemoveExtra(shinryouName, nDrugs, 1);
                    error(shinryouName + "重複", em, () ->
                            removeExtraShinryouMaster(visit, shinryoucode, 1)
                    );
                }
            } else {
                if (nDrugs > 0) {
                    String em = messageForRemoveExtra(shinryouName, nDrugs, 0);
                    error(shinryouName + "請求不可", em, () ->
                            removeExtraShinryouMaster(visit, shinryoucode, 0)
                    );
                }
            }
        });
    }

}
