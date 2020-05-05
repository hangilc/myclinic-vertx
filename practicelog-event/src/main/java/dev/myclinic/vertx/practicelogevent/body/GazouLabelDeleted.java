package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.GazouLabelDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class GazouLabelDeleted implements PracticeLogEventBody {

    public GazouLabelDTO deleted;

    public GazouLabelDeleted() {
    }

    public GazouLabelDeleted(GazouLabelDTO deleted) {
        this.deleted = deleted;
    }
}