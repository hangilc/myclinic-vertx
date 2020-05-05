package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DiseaseAdjDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DiseaseAdjDeleted implements PracticeLogEventBody {

    public DiseaseAdjDTO deleted;

    public DiseaseAdjDeleted() {
    }

    public DiseaseAdjDeleted(DiseaseAdjDTO deleted) {
        this.deleted = deleted;
    }
}