package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DrugAttrDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DrugAttrUpdated implements PracticeLogEventBody {

    public DrugAttrDTO prev;
    public DrugAttrDTO updated;

    public DrugAttrUpdated() {

    }

    public DrugAttrUpdated(DrugAttrDTO prev, DrugAttrDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}
