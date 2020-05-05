package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DiseaseAdjDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DiseaseAdjCreated implements PracticeLogEventBody {

    public DiseaseAdjDTO created;

    public DiseaseAdjCreated() {
    }

    public DiseaseAdjCreated(DiseaseAdjDTO created) {
        this.created = created;
    }
}