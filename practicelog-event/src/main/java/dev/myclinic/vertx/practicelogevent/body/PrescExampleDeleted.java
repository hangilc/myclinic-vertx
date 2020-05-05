package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PrescExampleDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PrescExampleDeleted implements PracticeLogEventBody {

    public PrescExampleDTO deleted;

    public PrescExampleDeleted() {

    }

    public PrescExampleDeleted(PrescExampleDTO deleted) {
        this.deleted = deleted;
    }
}
