package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShoukiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShoukiDeleted implements PracticeLogEventBody {

    public ShoukiDTO deleted;

    public ShoukiDeleted() {

    }

    public ShoukiDeleted(ShoukiDTO deleted) {
        this.deleted = deleted;
    }
}
