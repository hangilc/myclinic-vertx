package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DrugDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DrugCreated implements PracticeLogEventBody {

    public DrugDTO created;

    public DrugCreated() {
    }

    public DrugCreated(DrugDTO created) {
        this.created = created;
    }
}