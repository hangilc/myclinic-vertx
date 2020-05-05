package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DiseaseDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DiseaseCreated implements PracticeLogEventBody {

    public DiseaseDTO created;

    public DiseaseCreated() {
    }

    public DiseaseCreated(DiseaseDTO created) {
        this.created = created;
    }
}