package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ChargeDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ChargeUpdated implements PracticeLogEventBody {

    public ChargeDTO prev;
    public ChargeDTO updated;

    public ChargeUpdated() {
    }

    public ChargeUpdated(ChargeDTO prev, ChargeDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}