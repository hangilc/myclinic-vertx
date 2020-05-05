package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DrugDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DrugDeleted implements PracticeLogEventBody {

    public DrugDTO deleted;

    public DrugDeleted() {
    }

    public DrugDeleted(DrugDTO deleted) {
        this.deleted = deleted;
    }
}