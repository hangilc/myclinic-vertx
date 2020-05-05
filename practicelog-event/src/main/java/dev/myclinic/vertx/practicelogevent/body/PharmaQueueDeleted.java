package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PharmaQueueDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PharmaQueueDeleted implements PracticeLogEventBody {

    public PharmaQueueDTO deleted;

    public PharmaQueueDeleted() {
    }

    public PharmaQueueDeleted(PharmaQueueDTO deleted) {
        this.deleted = deleted;
    }
}
