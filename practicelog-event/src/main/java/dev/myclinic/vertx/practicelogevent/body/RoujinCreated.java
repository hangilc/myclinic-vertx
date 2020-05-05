package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.RoujinDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class RoujinCreated implements PracticeLogEventBody {

    public RoujinDTO created;

    public RoujinCreated() {
    }

    public RoujinCreated(RoujinDTO created) {
        this.created = created;
    }
}