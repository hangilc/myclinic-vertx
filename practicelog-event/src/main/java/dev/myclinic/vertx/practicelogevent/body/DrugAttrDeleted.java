package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DrugAttrDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DrugAttrDeleted implements PracticeLogEventBody {

    public DrugAttrDTO deleted;

    public DrugAttrDeleted() {

    }

    public DrugAttrDeleted(DrugAttrDTO deleted) {
        this.deleted = deleted;
    }
}
