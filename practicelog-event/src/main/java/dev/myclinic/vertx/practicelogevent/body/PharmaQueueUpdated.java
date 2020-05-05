package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PharmaQueueDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PharmaQueueUpdated implements PracticeLogEventBody {

    public PharmaQueueDTO prev;
    public PharmaQueueDTO updated;

    public PharmaQueueUpdated() {
    }

    public PharmaQueueUpdated(PharmaQueueDTO prev, PharmaQueueDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}
