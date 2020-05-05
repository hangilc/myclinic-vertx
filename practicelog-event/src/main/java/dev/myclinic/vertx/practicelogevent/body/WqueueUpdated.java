package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.WqueueDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class WqueueUpdated implements PracticeLogEventBody {

    public WqueueDTO prev;
    public WqueueDTO updated;

    public WqueueUpdated() {
    }

    public WqueueUpdated(WqueueDTO prev, WqueueDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}
