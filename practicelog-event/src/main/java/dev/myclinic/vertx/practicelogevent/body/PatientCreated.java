package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PatientDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PatientCreated implements PracticeLogEventBody {

    public PatientDTO created;

    public PatientCreated() {
    }

    public PatientCreated(PatientDTO created) {
        this.created = created;
    }
}
