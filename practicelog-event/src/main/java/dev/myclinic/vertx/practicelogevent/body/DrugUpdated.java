package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DrugDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DrugUpdated implements PracticeLogEventBody {

    public DrugDTO prev;
    public DrugDTO updated;

    public DrugUpdated() {
    }

    public DrugUpdated(DrugDTO prev, DrugDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}