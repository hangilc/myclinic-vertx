package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PatientDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PatientUpdated implements PracticeLogEventBody {

    public PatientDTO prev;
    public PatientDTO updated;

    public PatientUpdated() {
    }

    public PatientUpdated(PatientDTO prev, PatientDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}
