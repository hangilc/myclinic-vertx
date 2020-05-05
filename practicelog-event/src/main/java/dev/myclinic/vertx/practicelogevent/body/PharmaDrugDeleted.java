package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PharmaDrugDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PharmaDrugDeleted implements PracticeLogEventBody {

    public PharmaDrugDTO deleted;

    public PharmaDrugDeleted() {
    }

    public PharmaDrugDeleted(PharmaDrugDTO deleted) {
        this.deleted = deleted;
    }
}