package dev.myclinic.vertx.rcpt.check;

import dev.myclinic.vertx.dto.VisitFull2DTO;

class CheckChouzai extends CheckBase {

    CheckChouzai(Scope scope) {
        super(scope);
    }

    void check() {
        int choukiCount = countShinryouMasterInVisits(getShinryouMaster().調基);
        int shohousenCount = countShohousenGroupInVisits();
        if (shohousenCount > 0) {
            if (choukiCount > 0) {
                String fixMessage = messageForRemoveExtra("調基", choukiCount, 0);
                error("処方せん料、調基の同時算定", fixMessage, () ->
                        removeExtraShinryouMasterInVisits(getShinryouMaster().調基, 0)
                );
            }
        } else {
            if (countDrugInVisits(d -> true) == 0) {
                if (choukiCount > 0) {
                    String fixMessage = String.format("調基(%d件)を削除します。", choukiCount);
                    error("調基請求不可", fixMessage, () -> {
                        removeExtraShinryouMasterInVisits(getShinryouMaster().調基, 0);
                    });
                }
            } else {
                if (choukiCount > 1) {
                    String fixMessage = String.format("調基(%d件中%d件)を削除します。",
                            choukiCount, choukiCount - 1);
                    error("調基重複", fixMessage, () -> {
                        removeExtraShinryouMasterInVisits(getShinryouMaster().調基, 1);
                    });
                } else if (choukiCount == 0) {
                    error("調基抜け", "調基を追加します。", () -> {
                        VisitFull2DTO visit = findVisit(v -> v.drugs.size() > 0);
                        enterShinryou(visit, getShinryouMaster().調基);
                    });
                }
            }
        }
    }

}
