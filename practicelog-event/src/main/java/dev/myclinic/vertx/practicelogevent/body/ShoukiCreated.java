package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShoukiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShoukiCreated implements PracticeLogEventBody {

    public ShoukiDTO created;

    public ShoukiCreated() {

    }

    public ShoukiCreated(ShoukiDTO created) {
        this.created = created;
    }
}
