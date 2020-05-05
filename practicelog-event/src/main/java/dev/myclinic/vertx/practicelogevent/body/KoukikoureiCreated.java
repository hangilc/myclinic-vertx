package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.KoukikoureiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class KoukikoureiCreated implements PracticeLogEventBody {

    public KoukikoureiDTO created;

    public KoukikoureiCreated() {
    }

    public KoukikoureiCreated(KoukikoureiDTO created) {
        this.created = created;
    }
}