package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShahokokuhoDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShahokokuhoCreated implements PracticeLogEventBody {

    public ShahokokuhoDTO created;

    public ShahokokuhoCreated() {
    }

    public ShahokokuhoCreated(ShahokokuhoDTO created) {
        this.created = created;
    }
}
