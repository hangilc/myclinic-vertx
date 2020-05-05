package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.WqueueDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class WqueueCreated implements PracticeLogEventBody {

    public WqueueDTO created;

    public WqueueCreated() {
    }

    public WqueueCreated(WqueueDTO created) {
        this.created = created;
    }
}
