package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ChargeDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ChargeCreated implements PracticeLogEventBody {

    public ChargeDTO created;

    public ChargeCreated() {
    }

    public ChargeCreated(ChargeDTO created) {
        this.created = created;
    }
}