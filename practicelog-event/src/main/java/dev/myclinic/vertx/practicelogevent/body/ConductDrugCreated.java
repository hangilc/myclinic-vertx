package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductDrugDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductDrugCreated implements PracticeLogEventBody {

    public ConductDrugDTO created;

    public ConductDrugCreated() {
    }

    public ConductDrugCreated(ConductDrugDTO created) {
        this.created = created;
    }
}