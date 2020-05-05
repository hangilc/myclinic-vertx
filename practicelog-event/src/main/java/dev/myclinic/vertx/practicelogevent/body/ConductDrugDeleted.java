package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductDrugDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductDrugDeleted implements PracticeLogEventBody {

    public ConductDrugDTO deleted;

    public ConductDrugDeleted() {
    }

    public ConductDrugDeleted(ConductDrugDTO deleted) {
        this.deleted = deleted;
    }
}