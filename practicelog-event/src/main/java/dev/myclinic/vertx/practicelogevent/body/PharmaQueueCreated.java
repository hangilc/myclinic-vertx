package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PharmaQueueDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PharmaQueueCreated implements PracticeLogEventBody {

    public PharmaQueueDTO created;

    public PharmaQueueCreated() {
    }

    public PharmaQueueCreated(PharmaQueueDTO created) {
        this.created = created;
    }
}
