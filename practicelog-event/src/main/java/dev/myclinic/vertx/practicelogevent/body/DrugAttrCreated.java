package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.DrugAttrDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class DrugAttrCreated implements PracticeLogEventBody {

    public DrugAttrDTO created;

    public DrugAttrCreated() {

    }

    public DrugAttrCreated(DrugAttrDTO created){
        this.created = created;
    }

}
