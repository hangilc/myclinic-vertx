package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PrescExampleDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PrescExampleCreated implements PracticeLogEventBody {

    public PrescExampleDTO created;

    public PrescExampleCreated() {

    }

    public PrescExampleCreated(PrescExampleDTO created) {
        this.created = created;
    }
}
