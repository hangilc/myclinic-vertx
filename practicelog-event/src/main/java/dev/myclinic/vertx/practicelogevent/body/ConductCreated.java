package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductCreated implements PracticeLogEventBody {

    public ConductDTO created;

    public ConductCreated() {
    }

    public ConductCreated(ConductDTO created) {
        this.created = created;
    }
}