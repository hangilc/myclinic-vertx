package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DiseaseAdjDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DiseaseAdjUpdated implements PracticeLogEventBody {

    public DiseaseAdjDTO prev;
    public DiseaseAdjDTO updated;

    public DiseaseAdjUpdated() {
    }

    public DiseaseAdjUpdated(DiseaseAdjDTO prev, DiseaseAdjDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}