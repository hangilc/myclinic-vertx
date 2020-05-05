package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.KouhiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class KouhiDeleted implements PracticeLogEventBody {

    public KouhiDTO deleted;

    public KouhiDeleted() {
    }

    public KouhiDeleted(KouhiDTO deleted) {
        this.deleted = deleted;
    }
}