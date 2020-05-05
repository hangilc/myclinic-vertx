package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DiseaseDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DiseaseDeleted implements PracticeLogEventBody {

    public DiseaseDTO deleted;

    public DiseaseDeleted() {
    }

    public DiseaseDeleted(DiseaseDTO deleted) {
        this.deleted = deleted;
    }
}