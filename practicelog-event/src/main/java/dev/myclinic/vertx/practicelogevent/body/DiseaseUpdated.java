package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DiseaseDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DiseaseUpdated implements PracticeLogEventBody {

    public DiseaseDTO prev;
    public DiseaseDTO updated;

    public DiseaseUpdated() {
    }

    public DiseaseUpdated(DiseaseDTO prev, DiseaseDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}