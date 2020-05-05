package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PharmaDrugDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PharmaDrugCreated implements PracticeLogEventBody {

    public PharmaDrugDTO created;

    public PharmaDrugCreated() {
    }

    public PharmaDrugCreated(PharmaDrugDTO created) {
        this.created = created;
    }
}