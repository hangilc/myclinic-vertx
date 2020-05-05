package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductShinryouDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductShinryouCreated implements PracticeLogEventBody {

    public ConductShinryouDTO created;

    public ConductShinryouCreated() {
    }

    public ConductShinryouCreated(ConductShinryouDTO created) {
        this.created = created;
    }
}