package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.WqueueDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class WqueueDeleted implements PracticeLogEventBody {

    public WqueueDTO deleted;

    public WqueueDeleted() {
    }

    public WqueueDeleted(WqueueDTO deleted) {
        this.deleted = deleted;
    }
}
