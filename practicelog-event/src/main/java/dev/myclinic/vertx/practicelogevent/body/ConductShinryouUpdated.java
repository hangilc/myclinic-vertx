package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductShinryouDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductShinryouUpdated implements PracticeLogEventBody {

    public ConductShinryouDTO prev;
    public ConductShinryouDTO updated;

    public ConductShinryouUpdated() {
    }

    public ConductShinryouUpdated(ConductShinryouDTO prev, ConductShinryouDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}