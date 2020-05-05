package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.VisitDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class VisitUpdated implements PracticeLogEventBody {

    public VisitDTO prev;
    public VisitDTO updated;

    public VisitUpdated() {
    }

    public VisitUpdated(VisitDTO prev, VisitDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}