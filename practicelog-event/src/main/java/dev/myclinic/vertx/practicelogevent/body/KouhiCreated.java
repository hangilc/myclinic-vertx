package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.KouhiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class KouhiCreated implements PracticeLogEventBody {

    public KouhiDTO created;

    public KouhiCreated() {
    }

    public KouhiCreated(KouhiDTO created) {
        this.created = created;
    }
}