package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PharmaDrugDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PharmaDrugUpdated implements PracticeLogEventBody {

    public PharmaDrugDTO prev;
    public PharmaDrugDTO updated;

    public PharmaDrugUpdated() {
    }

    public PharmaDrugUpdated(PharmaDrugDTO prev, PharmaDrugDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}