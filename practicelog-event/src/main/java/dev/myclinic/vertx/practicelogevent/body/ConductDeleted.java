package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductDeleted implements PracticeLogEventBody {

    public ConductDTO deleted;

    public ConductDeleted() {
    }

    public ConductDeleted(ConductDTO deleted) {
        this.deleted = deleted;
    }
}