package dev.myclinic.vertx.hotlinelogevent.body;

import dev.myclinic.vertx.dto.HotlineDTO;
import dev.myclinic.vertx.hotlinelogevent.HotlineEventBody;

public class HotlineCreated implements HotlineEventBody {

    public HotlineDTO created;

    public HotlineCreated() {
    }

    public HotlineCreated(HotlineDTO created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "HotlineCreated{" +
                "created=" + created +
                '}';
    }
}
