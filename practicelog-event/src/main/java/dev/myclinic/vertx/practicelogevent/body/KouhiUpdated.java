package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.KouhiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class KouhiUpdated implements PracticeLogEventBody {

    public KouhiDTO prev;
    public KouhiDTO updated;

    public KouhiUpdated() {
    }

    public KouhiUpdated(KouhiDTO prev, KouhiDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}