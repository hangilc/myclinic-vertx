package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.GazouLabelDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class GazouLabelUpdated implements PracticeLogEventBody {

    public GazouLabelDTO prev;
    public GazouLabelDTO updated;

    public GazouLabelUpdated() {
    }

    public GazouLabelUpdated(GazouLabelDTO prev, GazouLabelDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}