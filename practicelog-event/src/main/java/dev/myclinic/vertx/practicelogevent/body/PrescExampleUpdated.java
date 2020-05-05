package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PrescExampleDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PrescExampleUpdated implements PracticeLogEventBody {

    public PrescExampleDTO prev;
    public PrescExampleDTO updated;

    public PrescExampleUpdated() {

    }

    public PrescExampleUpdated(PrescExampleDTO prev, PrescExampleDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}
