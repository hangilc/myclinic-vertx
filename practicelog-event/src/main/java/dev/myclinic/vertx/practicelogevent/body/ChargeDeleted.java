package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ChargeDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ChargeDeleted implements PracticeLogEventBody {

    public ChargeDTO deleted;

    public ChargeDeleted() {
    }

    public ChargeDeleted(ChargeDTO deleted) {
        this.deleted = deleted;
    }
}