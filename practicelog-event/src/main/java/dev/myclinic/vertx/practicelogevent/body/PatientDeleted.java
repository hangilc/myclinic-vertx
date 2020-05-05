package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PatientDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PatientDeleted implements PracticeLogEventBody {

    public PatientDTO deleted;

    public PatientDeleted() {
    }

    public PatientDeleted(PatientDTO deleted) {
        this.deleted = deleted;
    }
}
