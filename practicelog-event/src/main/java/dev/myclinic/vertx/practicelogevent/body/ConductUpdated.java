package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductUpdated implements PracticeLogEventBody {

    public ConductDTO prev;
    public ConductDTO updated;

    public ConductUpdated() {
    }

    public ConductUpdated(ConductDTO prev, ConductDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}