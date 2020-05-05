package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShinryouDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShinryouDeleted implements PracticeLogEventBody {

    public ShinryouDTO deleted;

    public ShinryouDeleted() {
    }

    public ShinryouDeleted(ShinryouDTO deleted) {
        this.deleted = deleted;
    }
}