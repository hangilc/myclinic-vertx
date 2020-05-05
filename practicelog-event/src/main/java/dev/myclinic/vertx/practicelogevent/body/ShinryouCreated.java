package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShinryouDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShinryouCreated implements PracticeLogEventBody {

    public ShinryouDTO created;

    public ShinryouCreated() {
    }

    public ShinryouCreated(ShinryouDTO created) {
        this.created = created;
    }
}