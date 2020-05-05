package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.RoujinDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class RoujinUpdated implements PracticeLogEventBody {

    public RoujinDTO prev;
    public RoujinDTO updated;

    public RoujinUpdated() {
    }

    public RoujinUpdated(RoujinDTO prev, RoujinDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}