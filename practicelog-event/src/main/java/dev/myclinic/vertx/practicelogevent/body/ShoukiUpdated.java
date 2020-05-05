package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShoukiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShoukiUpdated implements PracticeLogEventBody {

    public ShoukiDTO prev;
    public ShoukiDTO updated;

    public ShoukiUpdated() {

    }

    public ShoukiUpdated(ShoukiDTO prev, ShoukiDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}
