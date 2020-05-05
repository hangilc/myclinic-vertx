package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShinryouDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShinryouUpdated implements PracticeLogEventBody {

    public ShinryouDTO prev;
    public ShinryouDTO updated;

    public ShinryouUpdated() {
    }

    public ShinryouUpdated(ShinryouDTO prev, ShinryouDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}