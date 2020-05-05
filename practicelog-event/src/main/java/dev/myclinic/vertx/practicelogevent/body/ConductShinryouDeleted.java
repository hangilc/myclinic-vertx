package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductShinryouDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductShinryouDeleted implements PracticeLogEventBody {

    public ConductShinryouDTO deleted;

    public ConductShinryouDeleted() {
    }

    public ConductShinryouDeleted(ConductShinryouDTO deleted) {
        this.deleted = deleted;
    }
}