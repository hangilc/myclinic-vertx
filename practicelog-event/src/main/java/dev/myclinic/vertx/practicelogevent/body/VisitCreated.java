package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.VisitDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class VisitCreated implements PracticeLogEventBody {

    public VisitDTO created;

    public VisitCreated() {
    }

    public VisitCreated(VisitDTO created) {
        this.created = created;
    }
}