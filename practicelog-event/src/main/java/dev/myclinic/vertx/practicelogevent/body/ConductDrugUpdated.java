package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductDrugDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductDrugUpdated implements PracticeLogEventBody {

    public ConductDrugDTO prev;
    public ConductDrugDTO updated;

    public ConductDrugUpdated() {
    }

    public ConductDrugUpdated(ConductDrugDTO prev, ConductDrugDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}