package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.RoujinDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class RoujinDeleted implements PracticeLogEventBody {

    public RoujinDTO deleted;

    public RoujinDeleted() {
    }

    public RoujinDeleted(RoujinDTO deleted) {
        this.deleted = deleted;
    }
}