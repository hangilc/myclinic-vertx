package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.GazouLabelDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class GazouLabelCreated implements PracticeLogEventBody {

    public GazouLabelDTO created;

    public GazouLabelCreated() {
    }

    public GazouLabelCreated(GazouLabelDTO created) {
        this.created = created;
    }
}