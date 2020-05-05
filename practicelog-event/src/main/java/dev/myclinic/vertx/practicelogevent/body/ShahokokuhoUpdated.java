package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShahokokuhoDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShahokokuhoUpdated implements PracticeLogEventBody {

    public ShahokokuhoDTO prev;
    public ShahokokuhoDTO updated;

    public ShahokokuhoUpdated() {
    }

    public ShahokokuhoUpdated(ShahokokuhoDTO prev, ShahokokuhoDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}
