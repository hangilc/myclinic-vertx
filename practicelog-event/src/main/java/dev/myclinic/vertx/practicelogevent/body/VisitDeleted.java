package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.VisitDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class VisitDeleted implements PracticeLogEventBody {

    public VisitDTO deleted;

    public VisitDeleted() {
    }

    public VisitDeleted(VisitDTO deleted) {
        this.deleted = deleted;
    }
}